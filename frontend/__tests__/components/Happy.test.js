import { describe, it, expect } from 'vitest';
import { mount } from '@vue/test-utils';
import Happy from '@/components/Happy';

describe('Happy', () => {
  it('should render happy image', () => {
    expect(mount(Happy).html()).toMatchSnapshot();
  });
});
