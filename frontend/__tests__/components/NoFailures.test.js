import { describe, expect, it } from 'vitest';
import NoFailures from '@/components/NoFailures.vue';
import { mount } from '../test-utils';

describe('<NoFailures />', () => {
  it('should render no failures components', () => {
    expect(mount(NoFailures).html()).toMatchSnapshot();
  });
});
