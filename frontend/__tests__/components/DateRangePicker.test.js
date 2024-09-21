import { beforeEach, afterEach, describe, expect, it, vi } from 'vitest';
import preferences from '@/services/preferences';
import { mount, shallowMount } from '../test-utils';
import DateRangePicker from '@/components/DateRangePicker.vue';

describe('DateRangePicker', () => {
  const FIXED_SYSTEM_TIME = '2024-08-22T12:00:00.000Z';
  const DEFAULT_FROM = new Date('2024-08-14T18:30:00.000Z');
  const DEFAULT_TO = new Date('2024-08-22T18:29:59.999Z');

  beforeEach(() => {
    vi.spyOn(preferences, 'theme', 'get').mockReturnValueOnce('light');
    vi.useFakeTimers();
    vi.setSystemTime(Date.parse(FIXED_SYSTEM_TIME));
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('should provide correct props to date range component', () => {
    const wrapper = shallowMount(DateRangePicker);
    expect(wrapper.html()).toMatchSnapshot();
  });

  it('should render date range with selected last 7 days by default', () => {
    const wrapper = mount(DateRangePicker);

    expect(wrapper.html()).toMatchSnapshot();
    const emitted = wrapper.emitted();
    expect(emitted).toHaveProperty('date-range-updated');
    expect(emitted['date-range-updated']).toEqual([[DEFAULT_FROM, DEFAULT_TO]]);
  });

  it('should allow update selected date range', async () => {
    const wrapper = mount(DateRangePicker);

    await wrapper.find('[test-id="vuetify-date-range-picker"]').trigger('click');
    await wrapper.vm.$nextTick();

    await vi.waitUntil(() => document.querySelector('.v-date-picker') !== null);

    expect(document.querySelector('.v-date-picker').outerHTML).toMatchSnapshot();

    document.querySelector('[data-v-date="2024-08-05"] > button').click();

    await vi.waitUntil(() =>
      document.querySelector('.v-date-picker-month__day--selected[data-v-date="2024-08-05"]') !== null);

    document.querySelector('[data-v-date="2024-08-20"] > button').click();
    await vi.waitUntil(() =>
      document.querySelector('.v-date-picker-month__day--selected[data-v-date="2024-08-20"]') !== null);

    expect(document.querySelector('.v-date-picker').outerHTML).toMatchSnapshot();

    document.querySelector('.v-date-picker > .v-picker__actions > button:nth-child(2)').click();

    const emitted = wrapper.emitted();
    expect(emitted).toHaveProperty('date-range-updated');
    expect(emitted['date-range-updated']).toEqual([[DEFAULT_FROM, DEFAULT_TO], [
      new Date('2024-08-04T18:30:00.000Z'),
      new Date('2024-08-20T18:29:59.999Z')
    ]]);

    expect(wrapper.html()).toMatchSnapshot();
  });
});
