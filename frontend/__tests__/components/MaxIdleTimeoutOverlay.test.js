import { describe, expect, it, beforeEach, afterEach, vi } from 'vitest';
import { mount } from '../test-utils';

import MaxIdleTimeoutOverlay from '@/components/MaxIdleTimeoutOverlay.vue';

describe('<MaxIdleTimeoutOverlay />', () => {
  const { reload } = window.location;

  beforeEach(() => {
    Object.defineProperty(window, 'location', {
      writable: true,
      value: { reload: vi.fn() }
    });
  });

  afterEach(() => {
    window.location.reload = reload;
  });

  it('should render overlay', () => {
    expect(mount(MaxIdleTimeoutOverlay).html()).toMatchSnapshot();
  });

  it('should reload page when user clicks on reload button', async () => {
    const vueWrapper = mount(MaxIdleTimeoutOverlay);

    await vueWrapper.find('[test-id="reload-button"]').trigger('click');

    expect(window.location.reload).toHaveBeenCalledOnce();
  });
});
