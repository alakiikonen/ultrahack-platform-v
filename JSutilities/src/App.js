import React, { Component } from 'react';
import {ForecastData} from './VisualizationHelper.js';


const data = {
    ProductId: 1,
    timestamp: 1445426562,
    model: {
        x: [1445425617, 1445425673, 1445425729, 1445425785, 1445425841, 1445425897, 1445425953, 1445426009, 1445426065, 1445426121, 1445426177, 1445426233, 1445426289, 1445426345, 1445426401, 1445426457, 1445426513, 1445426569, 1445426625, 1445426682],
        y: [10.1521, 9.0078, 8.0424, 7.2371, 6.573, 6.0313, 5.5931, 5.2394, 4.9516, 4.7105, 4.4975, 4.2936, 4.0799, 3.8376, 3.5477, 3.1915, 2.75, 2.2044, 1.5357, 0.7093]
    }
};


var config = {
    chart: {
    type: 'spline'
},
title: {
    text: 'Warehouse product data'
},
subtitle: {
    text: 'Cool chart'
},
xAxis: {
    type: 'datetime',
        dateTimeLabelFormats: { // don't display the dummy year
        month: '%e. %b',
            year: '%b'
    },
    title: {
        text: 'Date'
    }
},
yAxis: {
    title: {
        text: 'Product stock (units)'
    },
    min: 0
},
tooltip: {
    headerFormat: '<b>{series.name}</b><br>',
        pointFormat: '{point.x:%e. %b}: {point.y:.2f} Units'
},

plotOptions: {
    spline: {
        marker: {
            enabled: true
        }
    }
},

series: [{
    name: "Estimate for Product id: "+data.ProductId,
    data: ForecastData(data)
}]};

var Highcharts = require('react-highcharts');



export default class TestiChart extends Component {
    render() {
        return (
            <div>
            <hi> Toimii!! </hi>
        <Highcharts config={config} ref="chart"></Highcharts>
            </div>
    );
    }
}
