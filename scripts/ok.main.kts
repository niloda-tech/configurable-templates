@file:Repository("https://repo1.maven.org/maven2/")
//@file:CompilerOptions("-Xcontext-parameters")
@file:DependsOn("../cot-dsl/build/libs/cot-dsl-1.0-SNAPSHOT-all.jar")

import kotlin.script.experimental.dependencies.DependsOn

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
