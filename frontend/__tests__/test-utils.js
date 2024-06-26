import { mount as vueMount } from '@vue/test-utils';
import { VApp } from 'vuetify/components';
import { h } from 'vue';
import getVuetify from '@/plugins/vuetify';

export const mount = (Component, options) =>
  vueMount(Component, {
    global: {
      plugins: [getVuetify()]
    },
    ...options
  });

export const mountWithWrapper = (Component, options = {}) =>
  mount(() =>
    h(VApp, () => [h(Component, options.props)]), { ...options, props: null });

export const promiseWithResolvers = () => {
  let resolve, reject;
  const promise = new Promise((_resolve, _reject) => {
    resolve = _resolve;
    reject = _reject;
  });

  return { promise, resolve, reject };
};
