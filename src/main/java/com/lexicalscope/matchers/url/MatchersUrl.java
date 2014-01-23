package com.lexicalscope.matchers.url;

import static org.hamcrest.Matchers.*;

import java.net.URL;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

public class MatchersUrl {
   public static Matcher<URL> urlHasProtocolFile() {
      return new TypeSafeDiagnosingMatcher<URL>() {
         @Override public void describeTo(final Description description) {
            description.appendText("URL with protocol ").appendValue("file");
         }

         @Override protected boolean matchesSafely(final URL item, final Description mismatchDescription) {
            mismatchDescription.appendText("URL with protocol ").appendValue(item.getProtocol());
            return item.getProtocol().equals(item.getProtocol());
         }
      };
   }

   public static Matcher<? super URL> urlPathEndsWith(final String suffix) {
      return urlPathMatches(endsWith(suffix));
   }

   public static Matcher<? super URL> urlPathContains(final String substring) {
      return urlPathMatches(containsString(substring));
   }

   public static Matcher<? super URL> urlPathMatches(final Matcher<String> pathMatcher) {
      return new TypeSafeDiagnosingMatcher<URL>() {
         @Override public void describeTo(final Description description) {
            description.appendText("URL with path matching ").appendDescriptionOf(pathMatcher);
         }

         @Override protected boolean matchesSafely(final URL item, final Description mismatchDescription) {
            mismatchDescription.appendText("URL with path matching ");
            pathMatcher.describeMismatch(item, mismatchDescription);
            return pathMatcher.matches(item.getPath());
         }
      };
   }
}
