import { mount as vueMount, shallowMount as vueShallowMount } from '@vue/test-utils';
import { VApp } from 'vuetify/components';
import { h } from 'vue';
import getVuetify from '@/plugins/vuetify';
import charts from '@/plugins/charts';

export const shallowMount = vueShallowMount;

export const mount = (Component, options) =>
  vueMount(Component, {
    global: {
      plugins: [getVuetify(), charts]
    },
    stubs: {
      transition: true
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

export const requestAnimationFrameAsPromise = () => new Promise((resolve) => requestAnimationFrame(resolve));

export const retryUntil = async (assertivePredicate, maxRetry = 3) =>
// eslint-disable-next-line no-async-promise-executor
  new Promise(async (resolve, reject) => {
    let retryCount = 0;
    let error;
    do {
      try {
        await assertivePredicate();
        resolve();
      } catch (e) {
        retryCount++;
        error = e;
      }
    } while (retryCount < maxRetry);

    reject(error);
  });
