import { describe, it, expect, beforeEach, vi } from 'vitest';
import { mountWithWrapper } from '../test-utils';
import DividerWithText from '@/components/DividerWithText';
import preferences from '@/services/preferences';

describe('<DividerWithText />', () => {
  beforeEach(() => {
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
  });

  it('should render with default thickness', () => {
    expect(mountWithWrapper(DividerWithText).html()).toMatchSnapshot();
  });

  it('should render with given thickness', () => {
    expect(mountWithWrapper(DividerWithText, {
      props: {
        thickness: 2
      }
    }).html()).toMatchSnapshot();
  });
});
