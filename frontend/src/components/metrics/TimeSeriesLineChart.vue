<template>
  <LineChart
    :id="id"
    :data="processedDataSets"
    :options="chartOptions"
  />
</template>

<script>
import { Line as LineChart } from 'vue-chartjs';
import { differenceInDays, format } from 'date-fns';

export default {
  name: 'TimeSeriesLineChart',
  components: {
    LineChart
  },
  props: {
    dataSet: {
      type: Object,
      required: true
    },
    startDateTime: {
      type: Date,
      required: true
    },
    endDateTime: {
      type: Date,
      required: true
    },
    timeExtractor: {
      type: Function,
      required: true
    },
    valueExtractor: {
      type: Function,
      required: true
    },
    id: {
      type: String,
      required: true
    },
    rateSeries: {
      type: Boolean,
      required: false,
      default: false
    }
  },
  data() {
    return {};
  },
  computed: {
    diffBetweenDays() {
      return differenceInDays(this.endDateTime, this.startDateTime);
    },
    aggregateBy() {
      if (this.diffBetweenDays <= 1) return 'hour';

      if (this.diffBetweenDays <= 30) return 'day';

      return 'month';
    },
    chartOptions() {
      const self = this;
      return {
        responsive: true,
        interaction: {
          mode: 'nearest'
        },
        plugins: {
          tooltip: {
            callbacks: {
              title(tooltipItem) {
                const [{ parsed: { x: epochDate } }] = tooltipItem;
                return self.formatTooltipTitle(new Date(epochDate));
              },
              label(tooltipItem) {
                const { dataset: { label }, formattedValue } = tooltipItem;
                return `${label}: ${formattedValue}`;
              }
            }
          }
        },
        scales: {
          x: {
            type: 'timeseries',
            max: this.endDateTime,
            min: this.startDateTime,
            time: {
              unit: this.aggregateBy
            },
            ticks: {
              major: {
                enabled: true
              },
              font: function (context) {
                if (context.tick && context.tick.major) {
                  return {
                    weight: 'bold'
                  };
                }
              }
            }
          },
          y: {
            beginAtZero: true,
            type: 'linear',
            suggestedMax: 1,
            suggestedMin: 0
          }
        }
      };
    },
    processedDataSets() {
      return {
        datasets: Object.keys(this.dataSet)
          .sort((a, b) => a - b)
          .map(key => {
            const groupedData = this.dataSet[key]
              .reduce((acc, currentValue) => {
                const extractedKey = this.extractKey(this.timeExtractor(currentValue));
                const existingData = acc[extractedKey] || { value: 0, totalNumberOfElements: 0 };
                return {
                  ...acc,
                  [extractedKey]: {
                    value: existingData.value + this.valueExtractor(currentValue),
                    totalNumberOfElements: existingData.totalNumberOfElements + 1
                  }
                };
              }, {});

            return {
              label: key,
              fill: false,
              data: Object.entries(groupedData)
                .map(([x, { value, totalNumberOfElements }]) => [x,
                  this.rateSeries ? (value / totalNumberOfElements) : value
                ])
                .map(([x, y]) => ({ x, y }))
                .sort(({ x: x1 }, { x: x2 }) => x1 - x2)
            };
          })
      };
    }
  },
  methods: {
    extractKey(dateString) {
      const date = new Date(dateString);
      switch (this.aggregateBy) {
        case 'hour':
          return format(date, "yyyy-MM-dd'T'HH");
        case 'day':
          return format(date, 'yyyy-MM-dd');
        case 'month':
          return format(date, 'yyyy-MM');
      }
    },
    formatTooltipTitle(date) {
      switch (this.aggregateBy) {
        case 'hour':
          return format(date, 'MMM d, yyyy, hh b..bb');
        case 'day':
          return format(date, 'MMM d, yyyy');
        default:
          return format(date, 'MMM, yyyy');
      }
    }
  }
};
</script>

<style scoped>

</style>
