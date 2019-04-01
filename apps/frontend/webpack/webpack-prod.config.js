const merge = require('webpack-merge');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const path = require('path');

const webpackCommon = require('./webpack-common.config.js');

const config = {
  plugins: [
      new HtmlWebpackPlugin({
            template: path.resolve(__dirname, '../../../../public/index.html')
          })
  ]
};

module.exports = merge(
  webpackCommon
  , config
);
