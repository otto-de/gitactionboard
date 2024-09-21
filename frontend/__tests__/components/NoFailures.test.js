import { describe, expect, it, beforeEach, vi } from 'vitest';
import NoFailures from '@/components/NoFailures.vue';
import { mount } from '../test-utils';
import preferences from '@/services/preferences';

describe('<NoFailures />', () => {
  beforeEach(() => {
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
  });

  it('should render no failures components', () => {
    expect(mount(NoFailures).html()).toMatchSnapshot();
  });
});
