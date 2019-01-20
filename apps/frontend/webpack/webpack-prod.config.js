const merge = require('webpack-merge');
const webpack = require('webpack');

const webpackCommon = require('./webpack-common.config.js');

const config = {
    plugins: [
        new webpack.optimize.UglifyJsPlugin()
    ]
};

module.exports = merge(
  webpackCommon
  , config
);
