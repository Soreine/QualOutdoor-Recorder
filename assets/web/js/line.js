"use strict";
/*##################################################*/
/*  Requires chart.js. */
/*##################################################*/

/** This Javascript file is designed to allow the use of a line chart
 * inside an Android WebView. Depends upon chart.js. */

/** The maximum number of data */
var MAX_HISTORY = 10;

/** This is the default configuration object used for the line chart */
var lineConfig = {
    // We want to draw a spline because it looks better <3
    chart: { type: 'spline' },

    // We have only one serie
    series: [ {data:[]} ],

    // Use a date format for the xAxis values
    xAxis: { type: 'datetime' },

    // No legend needed because we don't have multiple series
    legend: { enabled: false }
};

// Add our config to the chart.js default config
defaultConfig = merge_into(defaultConfig, lineConfig);

/** This function can be called by Android in order to initialize the
 * chart with the given config (in addition to the static
 * configuration defined here).
 * @param {Object} config An option object as defined in the
 * HighCharts documentation */
function initConfig(config) {
    // Merge the given config into the default one
    var finalConfig = merge_into(defaultConfig, config);
    // Initialize the chart with this config
    chart = new Highcharts.Chart(finalConfig);
}

/** Add a new value to the serie data. Shift the data if MAX_HISTORY
 * is reached.
 * @param {Number|Array} value The value to add. Taken as y value if a
 * Number, or (x,y) value pair if Array */
function addData(value) {
    var shift = false; // (chart.series[0].data.length >= MAX_HISTORY);
    chart.series[0].addPoint(value, true, shift);
};
