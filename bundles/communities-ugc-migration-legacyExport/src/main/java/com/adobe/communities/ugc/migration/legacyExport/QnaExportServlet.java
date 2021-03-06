/*************************************************************************
 *
 * ADOBE SYSTEMS INCORPORATED
 * Copyright 2015 Adobe Systems Incorporated
 * All Rights Reserved.
 *
 * NOTICE:  Adobe permits you to use, modify, and distribute this file in accordance with the
 * terms of the Adobe license agreement accompanying it.  If you have received this file from a
 * source other than Adobe, then your use, modification, or distribution of it requires the prior
 * written permission of Adobe.
 **************************************************************************/
package com.adobe.communities.ugc.migration.legacyExport;

import java.io.IOException;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.io.JSONWriter;

import com.adobe.cq.social.forum.api.Forum;
import com.adobe.cq.social.forum.api.Post;

@Component(label = "UGC Exporter for Qna Forum Data",
        description = "Moves qna data into json files for storage or re-import", specVersion = "1.0")
@Service
@Properties({@Property(name = "sling.servlet.paths", value = "/services/social/qna/export")})
public class QnaExportServlet extends ForumExportServlet {

    @Override
    protected void exportContent(final JSONWriter writer, final Forum forum, final ResourceResolver resolver)
        throws ServletException, IOException {
        try {
            writer.object();
            writer.key(ContentTypeDefinitions.LABEL_CONTENT_TYPE);
            writer.value(getContentType());
            writer.key(ContentTypeDefinitions.LABEL_CONTENT);
            writer.object();
            final List<Post> items = forum.getTopics(0, forum.getTopicCount(), true);
            for (Post post : items) {
                writer.key(post.getId());
                JSONWriter postObject = writer.object();
                UGCExportHelper.extractTopic(postObject, post, resolver, "social/qna/components/hbs/topic",
                    "social/qna/components/hbs/post", responseWriter);
                postObject.endObject();
            }
            writer.endObject();
            writer.endObject();
        } catch (final JSONException e) {
            throw new ServletException(e);
        } catch (final RepositoryException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected String getContentType() {
        return ContentTypeDefinitions.LABEL_QNA_FORUM;
    }
}
