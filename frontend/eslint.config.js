import eslint from '@eslint/js';
import eslintPluginVue from 'eslint-plugin-vue';
import globals from 'globals';
import neostandard from 'neostandard';

export default [
  {
    ignores: ['**/coverage', '**/dist']
  },
  eslint.configs.recommended,
  ...neostandard({ semi: true }),
  ...eslintPluginVue.configs['flat/recommended'],
  {
    files: ['**/*.vue', '**/*.js'],
    languageOptions: {
      ecmaVersion: 'latest',
      sourceType: 'module',
      globals: globals.browser,
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      'arrow-body-style': ['error', 'as-needed'],
      semi: [
        'error',
        'always'
      ],
      'max-len': [
        'error',
        120
      ],
      '@stylistic/space-before-function-paren': [
        'error',
        {
          anonymous: 'always',
          named: 'never',
          asyncArrow: 'always'
        }
      ]
    },
  }
];
