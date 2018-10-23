import com.springmarker.simplerpc.core.client.RpcClientFactory
import com.springmarker.simplerpc.enum.ProtocolType
import com.springmarker.simplerpc.protocol.http.HttpSender
import org.junit.jupiter.api.Test

/**
 * @author Springmarker
 * @date 2018/10/15 21:34
 */
class Main {


    @Test
    fun test() {
        val rpcClientFactory = RpcClientFactory(HttpSender(), arrayListOf(SimpleService::class.java))
        //val success = rpcClientFactory.add(SimpleService::class.java)
        val simpleService = rpcClientFactory.get(SimpleService::class.java)
        val test = simpleService!!.test()
        println()
    }


}
