import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mount } from '../test-utils';
import Spinner from '@/components/Spinner';
import preferences from '@/services/preferences';

describe('<Spinner />', () => {
  beforeEach(() => {
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
  });

  it('should render spinner', () => {
    expect(mount(Spinner).html()).toMatchSnapshot();
  });
});
