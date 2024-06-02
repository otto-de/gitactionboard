import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import DashboardHeader from '@/components/DashboardHeader';
import { getVersion } from '@/services/utils';
import { mountWithWrapper } from '../test-utils';

describe('<DashboardHeader />', () => {
  vi.mock('@/services/utils', () => {
    return {
      getVersion: vi.fn()
    };
  });

  beforeEach(() => {
    getVersion.mockReturnValue('3.1.0');
  });

  afterEach(() => {
    vi.clearAllMocks();
    vi.resetAllMocks();
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
