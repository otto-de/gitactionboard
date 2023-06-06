import { describe, it, expect, vi } from 'vitest';
import FailureGridCell from '@/components/FailureGridCell.vue';
import { mount } from '../test-utils';
import { getRelativeTime } from '@/services/utils';

describe('<FailureGridCell />', () => {
  vi.mock('@/services/utils', () => {
    return {
      getRelativeTime: vi.fn().mockReturnValue('10 months ago')
    };
  });

  it('should render grid cell', () => {
    const createdAt = '2022-08-15T02:20:34Z';

    const wrapper = mount(FailureGridCell, {
      props: {
        content: {
          id: '1234',
          name: 'test name',
          url: 'https://test.com',
          createdAt
        }
      }
    });

    expect(wrapper.html()).toMatchSnapshot();
    expect(getRelativeTime).toHaveBeenCalledOnce();
    expect(getRelativeTime).toHaveBeenCalledWith(createdAt);
  });
});
