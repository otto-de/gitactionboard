import { describe, expect, it, beforeEach, vi } from 'vitest';
import { mount, requestAnimationFrameAsPromise, retryUntil } from '../test-utils';
import GridCell from '@/components/GridCell.vue';
import preferences from '@/services/preferences';
import { getRelativeTime } from '@/services/utils';

describe('<GridCell />', () => {
  vi.mock('@/services/utils');

  const defaultProps = {
    name: 'webpack-cli :: webpack-cli :: Lint Commit Messages',
    url: 'https://github.com/webpack/webpack-cli/runs/7858502725',
    lastExecutedTime: '2022-08-16T03:45:02.000Z',
    buildMonitorViewEnabled: true
  };

  const rootId = `${defaultProps.name.replaceAll(/[\\:\s]/g, '-')}`;

  beforeEach(() => {
    getRelativeTime.mockReturnValue('2 years ago');
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
  });

  it.each([
    [undefined, undefined, undefined, undefined, undefined],
    [false, undefined, undefined, undefined, undefined],
    [true, undefined, undefined, undefined, undefined],
    [false, 'success', undefined, undefined, undefined],
    [false, 'SUCCESS', undefined, undefined, undefined],
    [false, 'unknown', undefined, undefined, undefined],
    [false, 'FAILURE', undefined, undefined, undefined],
    [false, 'random', undefined, undefined, undefined],
    [true, 'success', undefined, undefined, undefined],
    [true, 'SUCCESS', undefined, undefined, undefined],
    [true, 'unknown', undefined, undefined, undefined],
    [true, 'FAILURE', undefined, undefined, undefined],
    [true, 'random', undefined, undefined, undefined],
    [false, 'failure', undefined, undefined, true],
    [false, 'success', undefined, undefined, true],
    [true, 'failure', undefined, undefined, true],
    [true, 'success', undefined, undefined, true],
    [true, 'failure', undefined, undefined, false],
    [true, 'success', undefined, undefined, false],
    [false, 'failure', false, true, true]
  ])(
    'should render grid cell when inProgress=%s, status=%s, hidden=%s, displayToggleVisibility=%s, showRelativeTime=%s',
    (inProgress, status, hidden, displayToggleVisibility, showRelativeTime) => {
      const wrapper = mount(GridCell, {
        props: { ...defaultProps, inProgress, status, hidden, displayToggleVisibility, showRelativeTime }
      });
      expect(wrapper.html()).toMatchSnapshot();
    });

  it('should render grid cell when build monitor view is disabled', () => {
    const wrapper = mount(GridCell, {
      props: { ...defaultProps, buildMonitorViewEnabled: false }
    });
    expect(wrapper.html()).toMatchSnapshot();
  });

  it('should render in progress grid cell when build monitor view is disabled', () => {
    const wrapper = mount(GridCell, {
      props: { ...defaultProps, inProgress: true, buildMonitorViewEnabled: false }
    });
    expect(wrapper.html()).toMatchSnapshot();
  });

  it.each([
    [true, true, true],
    [false, true, true],
    [true, false, true],
    [false, false, true],
    [true, true, false],
    [false, false, false],
    [false, true, false], [true, false, false]
  ])('should flip on hover when hidden=%s, displayToggleVisibility=%s, showRelativeTime=%s',
    async (hidden, displayToggleVisibility, showRelativeTime) => {
      const wrapper = mount(GridCell, {
        props: { ...defaultProps, hidden, displayToggleVisibility, showRelativeTime }
      });
      await wrapper.find(`#${defaultProps.name.replaceAll(/[\\:\s]/g, '-')}`).trigger('mouseenter');
      await retryUntil(async () => {
        await requestAnimationFrameAsPromise();
        expect(wrapper.find(`[test-id="${rootId}-toolbar"]`).exists()).toBeTruthy();
      });
      expect(wrapper.html()).toMatchSnapshot();
    });

  it.each([
    [true, true, true],
    [false, true, true],
    [true, false, true],
    [false, false, true],
    [true, true, false],
    [false, false, false],
    [false, true, false], [true, false, false]
  ])('should flip back once mouse is moved away when hidden=%s, displayToggleVisibility=%s, showRelativeTime=%s',
    async (hidden, displayToggleVisibility, showRelativeTime) => {
      const wrapper = mount(GridCell, {
        props: { ...defaultProps, hidden, displayToggleVisibility, showRelativeTime }
      });

      await wrapper.find(`#${rootId}`).trigger('mouseenter');
      await retryUntil(async () => {
        await requestAnimationFrameAsPromise();
        expect(wrapper.find(`[test-id="${rootId}-toolbar"]`).exists()).toBeTruthy();
      });

      await wrapper.find(`#${rootId}`).trigger('mouseleave');
      await retryUntil(async () => {
        await requestAnimationFrameAsPromise();
        expect(wrapper.find(`[test-id="${rootId}-toolbar"]`).exists()).toBeFalsy();
      });
      expect(wrapper.html()).toMatchSnapshot();
    });

  it('should emit toggle visibility event when clicked on toggle icon', async () => {
    const wrapper = mount(GridCell, {
      props: { ...defaultProps, displayToggleVisibility: true }
    });

    await wrapper.find(`#${defaultProps.name.replaceAll(/[\\:\s]/g, '-')}`).trigger('mouseenter');
    await retryUntil(async () => {
      await requestAnimationFrameAsPromise();
      expect(wrapper.find(`[test-id="${rootId}-change-visibility-icon"]`).exists()).toBeTruthy();
    });

    await wrapper.find(`[test-id="${rootId}-change-visibility-icon"]`).trigger('click');
    const emitted = wrapper.emitted();
    expect(emitted).toHaveProperty('toggleVisibility');
    expect(emitted.toggleVisibility).toEqual([[defaultProps.name]]);
  });
});
