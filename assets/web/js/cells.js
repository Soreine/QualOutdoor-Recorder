"use strict";

/**
 * Recursively merge properties of two objects, overwriting obj1
 * properties with obj2 properties.
 * @param {Object} obj1 This object will received obj2 properties
 * @param {Object} obj2 The object whose properties are to be merged into the
 * first object.
 * @return {Object} The first object modified as the result of the merging
 */
function merge_into(obj1, obj2) {
  // For each property
  for (var prop in obj2) {
    try {
      // Property in destination object set; update its value.
      if (obj2[prop].constructor == Object) {
        obj1[prop] = merge_into(obj1[prop], obj2[prop]);

      } else {
        obj1[prop] = obj2[prop];

      }

    } catch(e) {
      // Property in destination object not set; create it and set its
      // value.
      obj1[prop] = obj2[prop];

    }
  }

    return obj1;
};

/** The maximum number of data */
var MAX_HISTORY = 10;

/** Add a new value to the serie data. Shift the data if MAX_HISTORY is reached.
 * @param {Number|Array} value The value to add. Taken as y value if a
 * Number, or (x,y) value pair if Array */
function addData(value) {
    var shift = (chart.series[0].data.length >= MAX_HISTORY);
    chart.series[0].addPoint(value, true, shift);
};


/** */
function updateCells(cells) {
    // For each 
    for(var cell in cells) {

    }
    // Update the data series with the new datas
    chart.series[0].setData(serie.data, true, false, false);
}

/** This is the configuration object used for the chart */
var highchartsConfig = {
    chart: { type: 'spline', renderTo: 'container' },

    series: [ {data:[]} ],

    xAxis: { type: 'datetime' },

    legend: { enabled: false },

    tooltip: { enabled: false },

    // Disable the lower right 'Highchart.com' credit
    credits: { enabled: false },

    // Disable initial animation
    plotOptions: { series: { animation: false } }
};

var chart = new Highcharts.Chart(highchartsConfig);

var serie = { data: [29.9, 71.5, 106.4, 148.5, 216.4, 194.1, 95.6, 54.4] };

var cell = {
    "timestamp": 515220412713405,
    "psc": 448,
    "is_registered": true,
    "mcc": 208,
    "signal_strength": {
        "asu": 7,
        "dBm": -99
    },
    "cell_type": 2,
    "mnc": 15,
    "cid": 220928935,
    "lac": 3310};


addData(35);
addData(35);


