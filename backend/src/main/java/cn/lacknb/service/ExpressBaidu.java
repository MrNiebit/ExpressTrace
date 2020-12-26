package cn.lacknb.service;

import cn.lacknb.exception.ExpressException;

/**
 * ClassName: ExpressBaidu <br/>
 * Description:  <br/>
 * date: 2020年04月15日 19:13 <br/>
 *
 * @author nianshao <br/>
 */
public interface ExpressBaidu {

    String getLatestMessage(String expressName, String expressNumber) throws ExpressException;

}
