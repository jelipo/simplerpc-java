import com.springmarker.simplerpc.client.RpcClientFactory
import org.junit.jupiter.api.Test

/**
 * @author Springmarker
 * @date 2018/10/15 21:34
 */
class Main {


    @Test
    fun test() {
        val main = Main()
        val javaClass = main.javaClass
        val method = javaClass.methods[0].annotations

        val rpcClientFactory = RpcClientFactory()
        val success = rpcClientFactory.add(SimpleService::class.java)
        val simpleService = rpcClientFactory.get(SimpleService::class.java)
        simpleService!!.test()
    }

}
