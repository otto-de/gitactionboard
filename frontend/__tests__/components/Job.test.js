import { describe, expect, it } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import GridCell from '@/components/GridCell.vue';
import Job from '@/components/Job.vue';

describe('<Job />', () => {
  const jobDetails = {
    activity: 'Sleeping',
    lastBuildStatus: 'Success',
    name: 'hello-world',
    webUrl: 'https://example.com/hello-world',
    triggeredEvent: 'push',
    lastBuildTime: '2022-08-15T02:20:34Z'
  };

  describe.each([
    [true],
    [false]
  ])('when build monitor view enabled = %s', (buildMonitorViewEnabled) => {
    it.each([
      ['Sleeping', 'Success', false, false, false],
      ['Sleeping', 'Success', true, false, false],
      ['Sleeping', 'Failure', false, true, false],
      ['Sleeping', 'Unknown', false, true, false],
      ['Sleeping', 'Exception', false, true, false],
      ['Building', 'Success', false, false, true],
      ['Building', 'Failure', false, false, true],
      ['Building', 'Unknown', false, false, true],
      ['Building', 'Exception', false, false, true],
      ['CheckingModifications', 'Success', false, false, false],
      ['CheckingModifications', 'Failure', false, true, false],
      ['CheckingModifications', 'Unknown', false, true, false],
      ['CheckingModifications', 'Exception', false, true, false]
    ])('should render job grid when activity is %s and last build status is %s and hidden is %s',
      async (activity, lastBuildStatus, hidden, showRelativeTime, inProgress) => {
        const wrapper = shallowMount(Job, {
          props: {
            content: { ...jobDetails, activity, lastBuildStatus },
            hidden,
            buildMonitorViewEnabled
          }
        });

        const gridCellComponent = wrapper.findComponent(GridCell);

        expect(gridCellComponent.exists()).toBeTruthy();

        expect(gridCellComponent.props()).toEqual({
          lastExecutedTime: jobDetails.lastBuildTime,
          url: jobDetails.webUrl,
          name: jobDetails.name,
          hidden,
          displayToggleVisibility: true,
          showRelativeTime,
          status: lastBuildStatus,
          inProgress,
          buildMonitorViewEnabled
        });

        await gridCellComponent.vm.$emit('toggle-visibility', jobDetails.name);
        const emitted = wrapper.emitted();
        expect(emitted).toHaveProperty('toggleVisibility');
        expect(emitted.toggleVisibility).toEqual([[jobDetails.name]]);
      });
  });
});
