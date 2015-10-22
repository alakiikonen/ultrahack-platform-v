/**
 * Created by jesse on 21.10.2015.
 */

export function ForecastData(object) {
    var time = object.model.x;
    var values = object.model.y;
    return time.map((t,i) => [t*1000, values[i]]);
}

export function MeasurementData(array) {
    return array.map((t,i) => [array[i].timestamp*1000, array[i].stock]);
}