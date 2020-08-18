package ru.grishagin.gxbtask.impl

import com.sun.javafx.geom.Vec2d
import com.sun.org.apache.xerces.internal.dom.ElementImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.xml.sax.InputSource
import ru.grishagin.gxbtask.api.Evaluator
import ru.grishagin.gxbtask.configuration.RestConfig
import ru.grishagin.gxbtask.model.EvaluatorParams
import java.io.StringReader
import javax.xml.parsers.DocumentBuilder

@Service("wolfram")
class WolframAlphaEvaluator(val restTemplate: RestTemplate, val restConfig: RestConfig,
                            val xmlBuilder: DocumentBuilder): Evaluator {

    override fun getValues(params: EvaluatorParams): List<Vec2d> {
        val requestParams = mapOf(Pair("appid", restConfig.id),
                Pair("input", prepareInput(params)))
        val response = restTemplate.getForEntity(restConfig.url, String::class.java, requestParams)
        return parseResponse(response.body, params)
    }

    private fun prepareInput(params: EvaluatorParams): String{
        return StringBuilder().apply {
            append("table[")
            append(params.expression)
            append(",{x,")
            append(params.from)
            append(",")
            append(params.to)
            append(",")
            append(params.step)
            append("}]")
        }.toString()
    }

    private fun parseResponse(response: String, params: EvaluatorParams): List<Vec2d>{
        val xml = xmlBuilder.parse(InputSource(StringReader(response)))
        val pods = xml.getElementsByTagName("pod")
        for(nodeIndex in 0..pods.length){
            val pod = pods.item(nodeIndex)
            if("Result".equals(pod.attributes.getNamedItem("title").nodeValue)){
                return (xml.getElementsByTagName("pod").item(nodeIndex) as ElementImpl).getElementsByTagName("plaintext").item(0).firstChild.nodeValue.split(",")
                        .map { it.replace("{", "") }
                        .map { it.replace("}", "") }
                        .map { it.trim() }
                        .mapIndexed{ i, value -> Pair(i, value) }
                        .filter { it.second.isNotEmpty() && !it.second.contains("i")  }
                        .map { Vec2d(params.from + params.step*it.first, it.second.toDouble())}
            }
        }
        return listOf()
    }
}