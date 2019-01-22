/* 
    JSPWiki - a JSP-based WikiWiki clone.

    Licensed to the Apache Software Foundation (ASF) under one
    or more contributor license agreements.  See the NOTICE file
    distributed with this work for additional information
    regarding copyright ownership.  The ASF licenses this file
    to you under the Apache License, Version 2.0 (the
    "License"); you may not use this file except in compliance
    with the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.  
 */
package com.ecyrd.jspwiki.tags;

import java.io.IOException;

import com.ecyrd.jspwiki.WikiPage;
import com.ecyrd.jspwiki.attachment.Attachment;

/**
 *  Includes the body, if the current page is of proper type.
 *
 *  <B>Attributes</B>
 *  <UL>
 *   <LI>type - either "page", "attachment" or "weblogentry"
 *  </UL>
 *
 *  @since 2.0
 */
public class PageTypeTag
    extends WikiTagBase
{
    private static final long serialVersionUID = 0L;
    
    private String m_type;

    public void initTag()
    {
        super.initTag();
        m_type = null;
    }

    public void setType( String arg )
    {
        m_type = arg.toLowerCase();
    }

    public final int doWikiStartTag()
        throws IOException
    {
        WikiPage   page   = m_wikiContext.getPage();

        if( page != null )
        {
            if( m_type.equals("attachment") && page instanceof Attachment )
            {
                return EVAL_BODY_INCLUDE;
            }
            
            if( m_type.equals("page") && !(page instanceof Attachment) )
            {
                return EVAL_BODY_INCLUDE;
            }

            if( m_type.equals("weblogentry") && !(page instanceof Attachment) && page.getName().indexOf("_blogentry_") != -1 )
            {
                return EVAL_BODY_INCLUDE;
            }
        }

        return SKIP_BODY;
    }
}
