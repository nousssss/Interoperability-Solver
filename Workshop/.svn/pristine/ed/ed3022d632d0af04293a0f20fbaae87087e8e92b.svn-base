package org.processmining.plugins.workshop.helloworld;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

public class HelloWorld4 {
        @Plugin(
                name = "My Hello World Plug-in Logging", 
                parameterLabels = {}, 
                returnLabels = { "Hello world string" }, 
                returnTypes = { String.class }, 
                userAccessible = true, help = "Produces the string: 'Hello world'"
        )
        @UITopiaVariant(
                affiliation = "My company", 
                author = "My name", 
                email = "My e-mail address"
        )
        public static String helloWorld(PluginContext context) {
                context.log("Started hello world plug-in", MessageLevel.DEBUG);
                return "Hello World";
        }
}