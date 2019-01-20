const merge = require('webpack-merge');

const webpackCommon = require('./webpack-common.config.js');

const config = {

};

module.exports = merge(
  webpackCommon
  , config
);
