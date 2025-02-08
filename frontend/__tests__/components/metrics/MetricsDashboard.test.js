import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import Spinner from '@/components/Spinner.vue';

import { mountWithWrapper, promiseWithResolvers, requestAnimationFrameAsPromise } from '../../test-utils';
import flushPromises from 'flush-promises';
import MetricsDashboard from '@/components/metrics/MetricsDashboard.vue';
import { fetchRepositoryNames, fetchWorkflowRunMetrics } from '@/services/apiService';
import preferences from '@/services/preferences';
import { getVersion } from '@/services/utils';

vi.mock('@/services/apiService');
vi.mock('@/services/utils');

describe('MetricsDashboard', () => {
  const FIXED_SYSTEM_TIME = '2024-08-22T12:00:00.000Z';
  const FROM = new Date('2024-08-14T18:30:00.000Z');
  const TO = new Date('2024-08-22T18:29:59.999Z');

  beforeEach(() => {
    getVersion.mockReturnValueOnce('5.0.0');
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
    vi.useFakeTimers();
    vi.setSystemTime(Date.parse(FIXED_SYSTEM_TIME));
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.useRealTimers();
  });

  const repoName1 = 'hello_world';
  const repoName2 = 'test';

  const mockSuccessfulWorkflowRunMetrics = () => {
    fetchWorkflowRunMetrics.mockImplementation((repoName) => Promise.resolve({
      test: [
        {
          workflowName: 'test',
          repoName,
          conclusion: 'FAILURE',
          runAttempt: 1,
          startedAt: '2024-09-06T06:06:04.000Z',
          completedAt: '2024-09-06T06:09:44.000Z',
          triggeredEvent: 'schedule'
        },
        {
          workflowName: 'test',
          repoName,
          conclusion: 'FAILURE',
          runAttempt: 1,
          startedAt: '2024-09-05T11:18:38.000Z',
          completedAt: '2024-09-05T11:22:06.000Z',
          triggeredEvent: 'push'
        }
      ]
    }));
  };

  it('should display spinner when component is loading', async () => {
    fetchRepositoryNames.mockReturnValue(promiseWithResolvers().promise);

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.findComponent(Spinner).exists());

    vi.useRealTimers();

    await requestAnimationFrameAsPromise();

    expect(wrapper.findComponent(MetricsDashboard).html()).toMatchSnapshot();
  });

  it('should display repository metrics expansion panels once the component is loaded', async () => {
    const repositoryNames = [repoName1, repoName2];
    fetchRepositoryNames.mockResolvedValueOnce(repositoryNames);

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.find("[test-id='metrics-dashboard-expansion-panels']").exists());

    expect(wrapper.findComponent(Spinner).exists()).toBeFalsy();

    repositoryNames.forEach(repositoryName =>
      expect(wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repositoryName}']`).exists())
        .toBeTruthy());
  });

  it('should display repository metrics when clicks on expansion icon', async () => {
    const repositoryNames = [repoName1, repoName2];
    fetchRepositoryNames.mockResolvedValueOnce(repositoryNames);

    mockSuccessfulWorkflowRunMetrics();

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`).exists());

    const repoName1ExpansionPanelWrapper = wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`);

    const repo1ExpansionIcon = repoName1ExpansionPanelWrapper.find('svg');
    expect(repo1ExpansionIcon.html()).toMatchSnapshot();

    await repo1ExpansionIcon.trigger('click');
    await vi.waitUntil(() => wrapper.find(`#${repoName1}-metrics`).exists());

    expect(wrapper.find("[test-id='metrics-dashboard-expansion-panels']")).toMatchSnapshot();
    expect(fetchWorkflowRunMetrics).toHaveBeenCalledOnce();
    expect(fetchWorkflowRunMetrics).toHaveBeenCalledWith(repoName1, FROM, TO);
    expect(wrapper.find(`#${repoName2}-metrics`).exists()).toBeFalsy();
  });

  it('should hide repository metrics when clicks on collapse icon', async () => {
    const repositoryNames = [repoName1, repoName2];
    fetchRepositoryNames.mockResolvedValueOnce(repositoryNames);

    mockSuccessfulWorkflowRunMetrics();

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`).exists());

    let repo1ExpansionIcon = wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`)
      .find('svg');
    expect(repo1ExpansionIcon.html()).toMatchSnapshot();
    await repo1ExpansionIcon.trigger('click');

    await vi.waitUntil(() => wrapper.find(`#${repoName1}-metrics`).exists());

    repo1ExpansionIcon = wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`)
      .find('svg');
    expect(repo1ExpansionIcon.html()).toMatchSnapshot();
    await repo1ExpansionIcon.trigger('click');
    await wrapper.vm.$nextTick();
    await flushPromises();
    await wrapper.vm.$nextTick();

    expect(wrapper.find(`[test-id='metrics-dashboard-expansion-panel-${repoName1}']`).html()).toMatchSnapshot();
    // TODO: Check RepositoryMetrics components gets unmounted
  });

  it('should display all repository metrics when clicks on expand all button', async () => {
    const repositoryNames = [repoName1, repoName2];
    fetchRepositoryNames.mockResolvedValueOnce(repositoryNames);

    mockSuccessfulWorkflowRunMetrics();

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.find("[test-id='metrics-dashboard-expansion-panels']").exists());

    await wrapper.find("[test-id='expand-all']").trigger('click');

    await vi.waitUntil(() => wrapper.find(`#${repoName1}-metrics`).exists());
    await vi.waitUntil(() => wrapper.find(`#${repoName2}-metrics`).exists());

    expect(fetchWorkflowRunMetrics).toHaveBeenCalledTimes(2);
    expect(fetchWorkflowRunMetrics).toHaveBeenCalledWith(repoName1, FROM, TO);
    expect(fetchWorkflowRunMetrics).toHaveBeenCalledWith(repoName2, FROM, TO);
  });

  it('should collapse all repository metrics when clicks on collapse all button', async () => {
    const repositoryNames = [repoName1, repoName2];
    fetchRepositoryNames.mockResolvedValueOnce(repositoryNames);

    mockSuccessfulWorkflowRunMetrics();

    const wrapper = mountWithWrapper(MetricsDashboard);

    await vi.waitUntil(() => wrapper.find("[test-id='metrics-dashboard-expansion-panels']").exists());

    await wrapper.find("[test-id='expand-all']").trigger('click');
    await wrapper.vm.$nextTick();
    await flushPromises();

    await vi.waitUntil(() => wrapper.find(`#${repoName1}-metrics`).exists());
    await vi.waitUntil(() => wrapper.find(`#${repoName2}-metrics`).exists());

    await wrapper.find("[test-id='collapse-all']").trigger('click');
    await wrapper.vm.$nextTick();
    await flushPromises();

    expect(wrapper.find("[test-id='metrics-dashboard-expansion-panels']").html()).toMatchSnapshot();
  });
});
