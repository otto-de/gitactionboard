import { describe, it, expect } from 'vitest';
import { mount } from '../test-utils';
import Spinner from '@/components/Spinner';

describe('<Spinner />', () => {
  it('should render spinner', () => {
    expect(mount(Spinner).html()).toMatchSnapshot();
  });
});
