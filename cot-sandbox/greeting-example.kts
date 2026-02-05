// Example Kotlin script that demonstrates COT template execution
// This script can be executed in the sandbox environment

@file:DependsOn("cot-dsl-1.0-SNAPSHOT.jar")

import com.niloda.cot.domain.dsl.cot
import com.niloda.cot.domain.generate.generate
import com.niloda.cot.domain.generate.RenderParams
import com.niloda.cot.domain.generate.renderConfigurable

// Define a simple greeting template
val cot = cot("Greeting") {
    "Hello, ".text
    Params.name ifTrueThen "World"
    "!".text
}

// Parameters for the template
val params = RenderParams.of(mapOf("name" to "Alice"))

// Generate output
val result = cot.generate(params, ::renderConfigurable)

// Print the result
result.fold(
    ifLeft = { error -> 
        System.err.println("Generation error: $error")
        kotlin.system.exitProcess(1)
    },
    ifRight = { output -> println(output) }
)
