package com.beteam.willu.post.querydsl;

import com.beteam.willu.post.entity.QPost;
import com.querydsl.core.BooleanBuilder;

public class PostExpressions {
	public static BooleanBuilder createBooleanExpression(String keyword, String criteria, boolean recruitment) {
		QPost post = QPost.post;
		BooleanBuilder builder = new BooleanBuilder();

		if (recruitment) {
			builder.and(post.recruitment.isTrue());
		}

		if ("nickname".equals(criteria)) {
			builder.and(post.user.nickname.containsIgnoreCase(keyword));
		} else if ("content".equals(criteria)) {
			builder.and(post.content.containsIgnoreCase(keyword));
		} else {
			builder.and(post.title.containsIgnoreCase(keyword));
		}

		return builder;
	}
}
