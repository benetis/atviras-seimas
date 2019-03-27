const merge = require('webpack-merge');

const webpackCommon = require('./webpack-common.config.js');

const config = {
    devServer: {
        proxy: {
          '/api': 'http://localhost:8080'
        }
  }
};

module.exports = merge(
  webpackCommon
  , config
);
