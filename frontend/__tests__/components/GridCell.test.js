import { describe, it, expect } from 'vitest';
import GridCell from '@/components/GridCell';
import { mount } from '../test-utils';

describe('<GridCell />', () => {
  it('should render grid cell', () => {
    const wrapper = mount(GridCell, {
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
