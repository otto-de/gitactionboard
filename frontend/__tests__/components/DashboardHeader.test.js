import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import DashboardHeader from '@/components/DashboardHeader';
import { getVersion } from '@/services/utils';
import { mountWithWrapper } from '../test-utils';
import preferences from '@/services/preferences';

describe('<DashboardHeader />', () => {
  vi.mock('@/services/utils', () => ({
    getVersion: vi.fn()
  }));

  beforeEach(() => {
    getVersion.mockReturnValue('3.1.0');
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
  });

  afterEach(() => {
    vi.clearAllMocks();
  });

  it.each([
    ['Workflow Jobs'], ['Exposed Secrets'], ['Code Standard Violations']
  ])('should render dashboard header for %s', async (subHeader) => {
    const wrapper = mountWithWrapper(DashboardHeader, {
      props: {
        subHeader
      }
    });

    await wrapper.vm.$nextTick();

    expect(wrapper.html()).toMatchSnapshot();
    expect(getVersion).toBeCalledTimes(1);
  });
});
