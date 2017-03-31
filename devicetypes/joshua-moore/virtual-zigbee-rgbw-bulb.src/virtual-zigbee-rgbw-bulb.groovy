/**
 *  Virtual ZigBee RGBW Bulb (Used for OSRAM RGBW light groups)
 *
 *  Copyright 2016 SmartThings
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Author: SmartThings
 *  Date: 2016-01-19
 *
 *  This DTH should serve as the generic DTH to handle RGBW ZigBee HA devices
 */

metadata {
    definition (name: "Virtual ZigBee RGBW Bulb", namespace: "joshua-moore", author: "JoshuaMoore") {
        capability "Actuator"
        capability "Refresh"
        capability "Switch"
        capability "Switch Level"
        capability "Color Temperature"
        capability "Color Control"
        
        // Thank you to Scott Gibson for the bulb temp display code
        attribute "bulbTemp", "string"
	}

	simulator {
		// TODO: define status and reply messages here
	}
    
    // UI tile definitions
    tiles(scale: 2) {
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL") {
                attributeState "level", action:"switch level.setLevel"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"color control.setColor"
            }
        }
        controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 4, height: 2, range:"(2700..6500)") {
            state "colorTemperature", action:"color temperature.setColorTemperature"
        }
        valueTile("colorTemp", "device.colorTemperature", decoration: "flat", width: 2, height: 2) {
            state "colorTemperature", label: '${currentValue} K',
				backgroundColors:[
					[value: 2900, color: "#FFA757"],
					[value: 3300, color: "#FFB371"],
					[value: 3700, color: "#FFC392"],
					[value: 4100, color: "#FFCEA6"],
					[value: 4500, color: "#FFD7B7"],
					[value: 4900, color: "#FFE0C7"],
					[value: 5300, color: "#FFE8D5"],
                    [value: 6600, color: "#FFEFE1"]
				]
        }
        standardTile("refresh", "device.switch", decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }

        main(["switch"])
        details(["switch", "colorTempSliderControl", "colorTemp", "refresh"])    
    }
}

def parse(String description) {}

def on() {
	// log.debug "on()"
	sendEvent(name: "switch", value: "on", isStateChange: true)
}

def off() {
	// log.debug "off()"
	sendEvent(name: "switch", value: "off", isStateChange: true) 
}

def refresh() {
    // log.debug "refresh"
    sendEvent(name: "refresh", value: null)
}

def setColorTemperature(value) {
	// log.debug "setColorTemperature(${value})"
    sendEvent(name: "colorTemperature", value: value, isStateChange: true)
    sendEvent(name: "bulbTemp", value: getBulbTemp(value))
}

def setLevel(value) {
	// log.debug "setLevel(${value})"
    sendEvent(name: "level", value: value, isStateChange: true)
}

def setColor(value){
	// log.debug "setColor(${value})"
	// stupid hack to set the state the first time this device is used. Won't select a color other than #FFFFFF without it.
	if (!state?.color) state.color = "#FF0000"
    sendEvent(name: "color", value: value.hex, data: value)
}

def setHue(value) {
	// log.debug "setHue(${value})"
    sendEvent(name: "hue", value: value, isStateChange: true)
}

def setSaturation(value) {
	// log.debug "setSaturation(${value})"
    sendEvent(name: "saturation", value: value, isStateChange: true)
}

// Again, thank you to Scott Gibson for the bulb temp code.
private getBulbTemp(value) {
    def s = "Soft White"
    
	if (value < 2900) {
    	return s
    } 
    else if (value < 3350) {
    	s = "Warm White"
        return s
    }
    else if (value < 3900) {
    	s = "Cool White"
        return s
    }
    else if (value < 4800) {
    	s = "Bright White"
        return s
    }
    else if (value < 5800) {
    	s = "Natural"
        return s
    }
    else {
    	s = "Daylight"
        return s
    }
}