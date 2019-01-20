const path = require('path');
const merge = require('webpack-merge');

const scalajsBundleConfig = require('./scalajs.webpack.config.js');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");

const extractSassAndCss = new ExtractTextPlugin({
  filename: "cssdeps.css"
});

const config = {
  entry: {
    loadScss: path.resolve(__dirname, '../../../../src/main/loadCssDeps.js')
  },
  module: {
    rules: [
      {
        test: /\.scss$/,
        use: extractSassAndCss.extract({
          use: [{
            loader: "css-loader"
          }, {
            loader: "sass-loader",
            options: {
              includePaths: [path.resolve(__dirname, '.')]
            }
          }],
          fallback: "style-loader"
        })
      },
      {
        test: /\.(png|jpg|gif)$/,
        use: [
          {
            loader: 'file-loader',
            options: {}
          }
        ]
      }]
  },
  plugins: [
    extractSassAndCss,
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, '../../../../public/index.html')
    })
  ],
};

module.exports = merge(
  scalajsBundleConfig
  , config
);
