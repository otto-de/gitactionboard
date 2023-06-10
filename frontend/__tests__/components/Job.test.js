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

  it.each([
    ['Sleeping', 'Success', false, false, 'success'],
    ['Sleeping', 'Success', true, false, 'success'],
    ['Sleeping', 'Failure', false, true, 'failure'],
    ['Sleeping', 'Unknown', false, true, 'unknown'],
    ['Sleeping', 'Exception', false, true, 'failure'],
    ['Building', 'Success', false, false, 'success building'],
    ['Building', 'Failure', false, false, 'failure building'],
    ['Building', 'Unknown', false, false, 'unknown building'],
    ['Building', 'Exception', false, false, 'failure building'],
    ['CheckingModifications', 'Success', false, false, 'success'],
    ['CheckingModifications', 'Failure', false, true, 'failure'],
    ['CheckingModifications', 'Unknown', false, true, 'unknown'],
    ['CheckingModifications', 'Exception', false, true, 'failure']
  ])('should render job grid when activity is %s and last build status is %s and hidden is %s',
    async (activity, lastBuildStatus, hidden, showRelativeTime, appendClassNames) => {
      const wrapper = shallowMount(Job, {
        props: {
          content: { ...jobDetails, activity, lastBuildStatus },
          hidden
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
        appendClassNames
      });

      await gridCellComponent.vm.$emit('toggle-visibility', jobDetails.name);
      const emitted = wrapper.emitted();
      expect(emitted).toHaveProperty('toggleVisibility');
      expect(emitted.toggleVisibility).toEqual([[jobDetails.name]]);
    });
});
