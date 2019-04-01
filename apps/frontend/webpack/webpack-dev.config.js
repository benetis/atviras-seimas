const merge = require('webpack-merge');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const webpackCommon = require('./webpack-common.config.js');
const path = require('path');

const config = {
    devServer: {
        proxy: {
          '/api': 'http://localhost:8080'
        }
  },
  plugins: [
      new HtmlWebpackPlugin({
            template: path.resolve(__dirname, '../../../../public/index_dev.html')
          })
  ]
};

module.exports = merge(
  webpackCommon
  , config
);
