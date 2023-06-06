import { describe, it, expect } from 'vitest';
import { mount } from '../test-utils';
import GridCell from '@/components/GridCell.vue';

describe('<GridCell />', () => {
  const defaultProps = {
    name: 'webpack-cli :: webpack-cli :: Lint Commit Messages',
    url: 'https://github.com/webpack/webpack-cli/runs/7858502725',
    lastExecutedTime: '2022-08-16T03:45:02.000Z'
  };

  it('should render grid cell', () => {
    const wrapper = mount(GridCell, {
      props: { ...defaultProps }
    });
    expect(wrapper.html()).toMatchSnapshot();
  });

  describe('Toggle Visibility', () => {
    it.each([
      [undefined],
      [false],
      [true]
    ])('should display toggle visibility icon with hide icon when hidden passed as %s', async (hidden) => {
      const wrapper = mount(GridCell, {
        props: { ...defaultProps, displayToggleVisibility: true, hidden }
      });

      expect(wrapper.html()).toMatchSnapshot();

      const toggleVisibilityTooltip = wrapper.find('[aria-describedby="v-tooltip-1"]');
      expect(toggleVisibilityTooltip.exists()).toBeTruthy();

      await toggleVisibilityTooltip.trigger('click');

      const emitted = wrapper.emitted();
      expect(emitted).toHaveProperty('toggleVisibility');
      expect(emitted.toggleVisibility).toEqual([[defaultProps.name]]);
    });
  });
});
