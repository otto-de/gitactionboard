import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { mountWithWrapper, promiseWithResolvers, requestAnimationFrameAsPromise, retryUntil } from '../test-utils';
import WorkflowDashboard from '@/components/WorkflowDashboard.vue';
import { fetchCctrayJson } from '@/services/apiService';
import Spinner from '@/components/Spinner.vue';
import flushPromises from 'flush-promises';
import Dashboard from '@/components/Dashboard.vue';
import preferences from '@/services/preferences';
import Job from '@/components/Job.vue';
import { getVersion } from '@/services/utils';
import NoFailures from '@/components/NoFailures.vue';
import MaxIdleTimeoutOverlay from '@/components/MaxIdleTimeoutOverlay.vue';

vi.mock('@/services/apiService');
vi.mock('@/services/utils');

describe('<WorkflowDashboard />', () => {
  const jobDetails1 = {
    name: 'webpack-cli :: Cancel :: Cancel Previous Runs',
    activity: 'Sleeping',
    lastBuildStatus: 'Failure',
    lastBuildLabel: '4',
    lastBuildTime: '2022-08-15T02:20:34.000Z',
    webUrl: 'https://github.com/webpack/webpack-cli/runs/7831157675?check_suite_focus=true',
    triggeredEvent: 'schedule'
  };

  const jobDetails2 = {
    name: 'webpack-cli :: webpack-cli :: Lint Commit Messages',
    activity: 'Sleeping',
    lastBuildStatus: 'Success',
    lastBuildLabel: '7613',
    lastBuildTime: '2022-08-16T03:45:02.000Z',
    webUrl: 'https://github.com/webpack/webpack-cli/runs/7858502725?check_suite_focus=true',
    triggeredEvent: 'push'
  };

  const jobDetails1RootId = jobDetails1.name.replaceAll(/[\\:\s]/g, '-');
  const hiddenContentsTitleBarTestId = '[test-id="job-hidden-contents-tool-bar"]';

  const clickOnJobDetailsVisibilityToggleIcon = async (wrapper, jobDetailsRootId) => {
    const jobDetailsCard = wrapper.find(`#${jobDetailsRootId}`);
    expect(jobDetailsCard.exists()).toBeTruthy();

    await jobDetailsCard.trigger('mouseenter');

    await retryUntil(async () => {
      await requestAnimationFrameAsPromise();
      expect(wrapper.find(`[test-id="${jobDetailsRootId}-toolbar"]`).exists()).toBeTruthy();
    });

    wrapper
      .find(`[test-id="${jobDetailsRootId}-change-visibility-icon"]`)
      .trigger('click');
    await wrapper.vm.$nextTick();
    await flushPromises();
  };

  let enableBuildMonitorViewSpy;
  let showBuildsDueToTriggeredEventsSpy;

  beforeEach(() => {
    getVersion.mockReturnValueOnce('3.3.0');
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
    enableBuildMonitorViewSpy = vi.spyOn(preferences, 'enableBuildMonitorView', 'get').mockReturnValueOnce(true);
    showBuildsDueToTriggeredEventsSpy = vi.spyOn(preferences, 'showBuildsDueToTriggeredEvents', 'get')
      .mockReturnValueOnce([]);
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.restoreAllMocks();
  });

  it('should render spinner while fetching data', async () => {
    fetchCctrayJson.mockReturnValue(promiseWithResolvers().promise);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await vi.waitUntil(() => workflowDashboardWrapper.findComponent(Spinner).exists());
  });

  it('should render dashboard once data is fetched', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);

    const { promise, resolve } = promiseWithResolvers();

    fetchCctrayJson.mockReturnValue(promise);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await vi.waitUntil(() => workflowDashboardWrapper.findComponent(Spinner).exists());

    resolve([jobDetails1, jobDetails2]);

    await flushPromises();

    expect(workflowDashboardWrapper.findComponent(Spinner).exists()).toBeFalsy();

    const dashboardComponent = workflowDashboardWrapper.findComponent(Dashboard);

    expect(dashboardComponent.exists()).toBeTruthy();
    expect(dashboardComponent.props()).toStrictEqual({
      contentDisplayer: 'Job',
      enableMaxIdleTimeOptimization: true,
      hideByKey: 'name',
      maxIdleTime: 10,
      nameOfItems: 'job(s)',
      fetchContents: expect.any(Function)
    });
  });

  it.each([
    [true],
    [false]
  ])('should render dashboard with all job details when build monitor view enabled = %s',
    async (buildMonitorViewEnabled) => {
      enableBuildMonitorViewSpy.mockRestore();
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});
      vi.spyOn(preferences, 'enableBuildMonitorView', 'get').mockReturnValueOnce(buildMonitorViewEnabled);

      fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      const jobComponents = workflowDashboardWrapper.findAllComponents(Job);

      expect(jobComponents).length(2);
      await workflowDashboardWrapper.vm.$nextTick();
      await retryUntil(async () => {
        await requestAnimationFrameAsPromise();
        expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeTruthy();
      });
      expect(workflowDashboardWrapper.html()).toMatchSnapshot();
    });

  it('should hide success job details', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(false);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    const jobComponents = workflowDashboardWrapper.findAllComponents(Job);

    expect(jobComponents).length(1);
    expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeTruthy();
    expect(workflowDashboardWrapper.find(`#${jobDetails2.name.replaceAll(/[\\:\s]/g, '-')}`).exists()).toBeFalsy();
  });

  it('should hide job details when user clicks on hide icon', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeFalsy();

    await clickOnJobDetailsVisibilityToggleIcon(workflowDashboardWrapper, jobDetails1RootId);

    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
    expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeFalsy();
  });

  it('should hide job details depends previous config', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({ 'job(s)': [jobDetails1.name] });

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeFalsy();
  });

  it('should display hidden jobs when user clicks on show', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({ 'job(s)': [jobDetails1.name] });

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    workflowDashboardWrapper.find('[test-id="job-hidden-contents-tool-bar-icon"]').trigger('click');
    await workflowDashboardWrapper.vm.$nextTick();
    await flushPromises();
    expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeTruthy();
    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
  });

  it('should put hidden jobs to normal job list when user clicks on hide icon', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({ 'job(s)': [jobDetails1.name] });

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    workflowDashboardWrapper.find('[test-id="job-hidden-contents-tool-bar-icon"]').trigger('click');
    await workflowDashboardWrapper.vm.$nextTick();
    await flushPromises();
    await clickOnJobDetailsVisibilityToggleIcon(workflowDashboardWrapper, jobDetails1RootId);
    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeFalsy();
    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
  });

  it('should not remove hidden jobs toolbar when hidden jobs available', async () => {
    vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
    vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
    vi.spyOn(preferences, 'hiddenElements', 'get')
      .mockReturnValueOnce({ 'job(s)': [jobDetails1.name, jobDetails2.name] });

    fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

    const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

    await flushPromises();

    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    workflowDashboardWrapper.find('[test-id="job-hidden-contents-tool-bar-icon"]').trigger('click');
    await workflowDashboardWrapper.vm.$nextTick();
    await flushPromises();
    await clickOnJobDetailsVisibilityToggleIcon(workflowDashboardWrapper, jobDetails1RootId);
    expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    expect(workflowDashboardWrapper.html()).toMatchSnapshot();
  });

  it.each([
    ['branch_protection_rule', [{ ...jobDetails1, triggeredEvent: 'branch_protection_rule' }, jobDetails2]],
    ['check_run', [{ ...jobDetails1, triggeredEvent: 'check_run' }, jobDetails2]],
    ['check_suite', [{ ...jobDetails1, triggeredEvent: 'check_suite' }, jobDetails2]],
    ['create', [{ ...jobDetails1, triggeredEvent: 'create' }, jobDetails2]],
    ['delete', [{ ...jobDetails1, triggeredEvent: 'delete' }, jobDetails2]],
    ['deployment', [{ ...jobDetails1, triggeredEvent: 'deployment' }, jobDetails2]],
    ['deployment_status', [{ ...jobDetails1, triggeredEvent: 'deployment_status' }, jobDetails2]],
    ['discussion', [{ ...jobDetails1, triggeredEvent: 'discussion' }, jobDetails2]],
    ['discussion_comment', [{ ...jobDetails1, triggeredEvent: 'discussion_comment' }, jobDetails2]],
    ['fork', [{ ...jobDetails1, triggeredEvent: 'fork' }, jobDetails2]],
    ['gollum', [{ ...jobDetails1, triggeredEvent: 'gollum' }, jobDetails2]],
    ['issue_comment', [{ ...jobDetails1, triggeredEvent: 'issue_comment' }, jobDetails2]],
    ['issues', [{ ...jobDetails1, triggeredEvent: 'issues' }, jobDetails2]],
    ['label', [{ ...jobDetails1, triggeredEvent: 'label' }, jobDetails2]],
    ['member', [{ ...jobDetails1, triggeredEvent: 'member' }, jobDetails2]],
    ['merge_group', [{ ...jobDetails1, triggeredEvent: 'merge_group' }, jobDetails2]],
    ['milestone', [{ ...jobDetails1, triggeredEvent: 'milestone' }, jobDetails2]],
    ['page_build', [{ ...jobDetails1, triggeredEvent: 'page_build' }, jobDetails2]],
    ['project', [{ ...jobDetails1, triggeredEvent: 'project' }, jobDetails2]],
    ['project_card', [{ ...jobDetails1, triggeredEvent: 'project_card' }, jobDetails2]],
    ['project_column', [{ ...jobDetails1, triggeredEvent: 'project_column' }, jobDetails2]],
    ['public', [{ ...jobDetails1, triggeredEvent: 'public' }, jobDetails2]],
    ['pull_request', [{ ...jobDetails1, triggeredEvent: 'pull_request' }, jobDetails2]],
    ['pull_request_review', [{ ...jobDetails1, triggeredEvent: 'pull_request_review' }, jobDetails2]],
    ['pull_request_review_comment', [{ ...jobDetails1, triggeredEvent: 'pull_request_review_comment' }, jobDetails2]],
    ['pull_request_target', [{ ...jobDetails1, triggeredEvent: 'pull_request_target' }, jobDetails2]],
    ['push', [{ ...jobDetails1, triggeredEvent: 'push' }, { ...jobDetails2, triggeredEvent: 'schedule' }]],
    ['registry_package', [{ ...jobDetails1, triggeredEvent: 'registry_package' }, jobDetails2]],
    ['release', [{ ...jobDetails1, triggeredEvent: 'release' }, jobDetails2]],
    ['status', [{ ...jobDetails1, triggeredEvent: 'status' }, jobDetails2]],
    ['watch', [{ ...jobDetails1, triggeredEvent: 'watch' }, jobDetails2]],
    ['workflow_call', [{ ...jobDetails1, triggeredEvent: 'workflow_call' }, jobDetails2]],
    ['workflow_dispatch', [{ ...jobDetails1, triggeredEvent: 'workflow_dispatch' }, jobDetails2]],
    ['workflow_run', [{ ...jobDetails1, triggeredEvent: 'workflow_run' }, jobDetails2]],
    ['repository_dispatch', [{ ...jobDetails1, triggeredEvent: 'repository_dispatch' }, jobDetails2]],
    ['schedule', [jobDetails1, jobDetails2]]
  ])('should show only job details related to filtered triggered event %s',
    async (triggeredEvent, availableJobDetails) => {
      showBuildsDueToTriggeredEventsSpy.mockRestore();
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValue(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValue(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValue(10);
      vi.spyOn(preferences, 'showBuildsDueToTriggeredEvents', 'get').mockReturnValue([triggeredEvent]);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValue([]);

      fetchCctrayJson.mockResolvedValueOnce(availableJobDetails);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      const jobComponents = workflowDashboardWrapper.findAllComponents(Job);

      expect(jobComponents).length(1);
      expect(workflowDashboardWrapper.find(`#${jobDetails1RootId}`).exists()).toBeTruthy();
      expect(workflowDashboardWrapper.find(`#${jobDetails2.name.replaceAll(/[\\:\s]/g, '-')}`).exists()).toBeFalsy();
    });

  describe('No contents', () => {
    it('should show no contents when there is no job available', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

      fetchCctrayJson.mockResolvedValueOnce([]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();
      await requestAnimationFrameAsPromise();

      expect(workflowDashboardWrapper.findComponent(NoFailures).exists()).toBeTruthy();
      expect(workflowDashboardWrapper.html()).toMatchSnapshot();
    });

    it('should show no contents when all jobs are success and showHealthyBuilds is set to false', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(false);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

      fetchCctrayJson.mockResolvedValueOnce([{ ...jobDetails1, lastBuildStatus: 'Success' }, jobDetails2]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      expect(workflowDashboardWrapper.findComponent(NoFailures).exists()).toBeTruthy();
    });

    it('should show no contents when all jobs are filtered due to showBuildsDueToTriggeredEvents config', async () => {
      showBuildsDueToTriggeredEventsSpy.mockRestore();
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'showBuildsDueToTriggeredEvents', 'get').mockReturnValue(['repository_dispatch']);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

      fetchCctrayJson.mockResolvedValueOnce([jobDetails1, jobDetails2]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      expect(workflowDashboardWrapper.findComponent(NoFailures).exists()).toBeTruthy();
    });

    it('should not show no contents if some jobs are hidden', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({ 'job(s)': [jobDetails1.name] });

      fetchCctrayJson.mockResolvedValueOnce([jobDetails1]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      expect(workflowDashboardWrapper.findComponent(NoFailures).exists()).toBeFalsy();
      expect(workflowDashboardWrapper.find(hiddenContentsTitleBarTestId).exists()).toBeTruthy();
    });
  });

  describe('Timers', () => {
    beforeEach(() => {
      vi.spyOn(preferences, 'showBuildsDueToTriggeredEvents', 'get').mockReturnValueOnce([]);
      vi.useFakeTimers();
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    it('should fetch data after every certain interval', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

      fetchCctrayJson.mockResolvedValue([jobDetails1]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      vi.advanceTimersByTime(10 * 1000);
      expect(fetchCctrayJson).toHaveBeenCalledTimes(3);
      expect(workflowDashboardWrapper.findAllComponents(Job)).length(1);
    });

    it('should fetch data after every certain interval indefinitely', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(false);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(0);

      fetchCctrayJson.mockResolvedValue([jobDetails1]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();

      const oneDay = 24 * 60 * 1000 * 60;
      vi.advanceTimersByTime(oneDay);
      expect(fetchCctrayJson).toHaveBeenCalledTimes(oneDay / 5000 + 1);
      expect(workflowDashboardWrapper.findAllComponents(Job)).length(1);
    });

    it('should show stopped automated page refresh popup', async () => {
      vi.spyOn(preferences, 'showHealthyBuilds', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'enableMaxIdleTimeOptimization', 'get').mockReturnValueOnce(true);
      vi.spyOn(preferences, 'maxIdleTime', 'get').mockReturnValueOnce(10);
      vi.spyOn(preferences, 'hiddenElements', 'get').mockReturnValueOnce({});

      fetchCctrayJson.mockResolvedValue([jobDetails1]);

      const workflowDashboardWrapper = mountWithWrapper(WorkflowDashboard);

      await flushPromises();
      expect(workflowDashboardWrapper.findComponent(MaxIdleTimeoutOverlay).exists()).toBeFalsy();
      vi.advanceTimersByTime(11 * 1000 * 60);
      await workflowDashboardWrapper.vm.$nextTick();
      await flushPromises();
      expect(workflowDashboardWrapper.findComponent(MaxIdleTimeoutOverlay).exists()).toBeTruthy();
      fetchCctrayJson.mockReset();
      vi.advanceTimersByTime(11 * 1000 * 60);
      await workflowDashboardWrapper.vm.$nextTick();
      await flushPromises();
      expect(fetchCctrayJson).not.toHaveBeenCalled();
    });
  });
});
