import {
  Chart as ChartJS,
  Legend,
  LinearScale,
  LineElement,
  PointElement,
  TimeScale,
  TimeSeriesScale,
  Title,
  Tooltip
} from 'chart.js';

import 'chartjs-adapter-date-fns';
import autocolors from 'chartjs-plugin-autocolors';

import preferences from '@/services/preferences';

const getChartColors = () => {
  const isDark = preferences.theme === 'dark';

  return {
    backgroundColor: isDark ? 'rgba(255,255,255, 0.6)' : 'rgba(0,0,0,0.1)',
    borderColor: isDark ? 'rgba(255,255,255,0.6)' : 'rgba(0,0,0,0.1)',
    color: isDark ? '#EEE' : '#666'
  };
};

export default () => {
  const { backgroundColor, borderColor, color } = getChartColors();

  ChartJS.defaults.backgroundColor = backgroundColor;
  ChartJS.defaults.borderColor = borderColor;
  ChartJS.defaults.color = color;

  ChartJS.register(
    Title, Tooltip, Legend, LineElement, PointElement,
    LinearScale, TimeScale, TimeSeriesScale, autocolors
  );
};
