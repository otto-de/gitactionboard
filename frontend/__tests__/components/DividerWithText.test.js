import { describe, it, expect } from 'vitest';
import { mountWithWrapper } from '../test-utils';
import DividerWithText from '@/components/DividerWithText';

describe('<DividerWithText />', () => {
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
