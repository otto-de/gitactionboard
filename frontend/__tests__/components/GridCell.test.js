import { describe, it, expect } from 'vitest';
import { shallowMount } from '@vue/test-utils';
import GridCell from '@/components/GridCell';

describe('GridCell', () => {
  it('should render grid cell', () => {
    const wrapper = shallowMount(GridCell, {
      props: {
        content: {
          id: '1234',
          name: 'test name',
          url: 'https://test.com'
        }
      }
    });

    expect(wrapper.html()).toMatchSnapshot();
  });
});
