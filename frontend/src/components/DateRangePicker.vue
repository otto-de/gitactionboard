<template>
  <v-date-input
    v-model="dateRange"
    test-id="vuetify-date-range-picker"
    elevation="24"
    max-width="350px"
    multiple="range"
    :label="label"
    :max="maxAllowedDate"
    :min="minAllowedDate"
    :rules="rules"
    :disabled="disabled"
    @update:model-value="dateRangeUpdated"
  />
</template>

<script>
import { watch } from 'vue';
import { subDays, endOfDay, startOfDay, eachDayOfInterval } from 'date-fns';

export default {
  name: 'DateRangePicker',
  props: {
    label: {
      type: String,
      required: false,
      default: ''
    },
    from: {
      type: Date,
      required: false,
      default: () => startOfDay(subDays(new Date(), 7))
    },
    to: {
      type: Date,
      required: false,
      default: () => endOfDay(new Date())
    },
    disabled: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  emits: ['date-range-updated'],
  data() {
    this.emitDateRangeUpdated(this.from, this.to);
    return {
      maxAllowedDate: new Date(),
      minAllowedDate: new Date(2024, 0, 1),
      dateRange: eachDayOfInterval({ start: this.from, end: this.to }),
      rules: [
        (updatedInput) => !!updatedInput
      ]
    };
  },
  mounted() {
    watch(() => [this.from, this.to], ([newFrom, newTo]) => {
      this.dateRange = [newFrom, newTo];
    });
  },
  methods: {
    dateRangeUpdated(updatedDateRange) {
      if (updatedDateRange.length !== 0) {
        this.emitDateRangeUpdated(new Date(updatedDateRange[0]),
          new Date(updatedDateRange[updatedDateRange.length - 1]));
      }
    },
    emitDateRangeUpdated(from, to) {
      this.$emit('date-range-updated', startOfDay(from), endOfDay(to));
    }
  }
};
</script>

<style scoped>

</style>
