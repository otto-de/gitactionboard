import { expect } from 'vitest';
import vueSnapshotSerializer from 'jest-serializer-vue-tjw';
import 'vitest-canvas-mock';

class ResizeObserverStub {
  observe() { }
  unobserve() { }
  disconnect() { }
}

window.ResizeObserver = window.ResizeObserver || ResizeObserverStub;
expect.addSnapshotSerializer(vueSnapshotSerializer);
global.CSS = { supports: () => false };
process.env.TZ = 'Asia/Kolkata';
