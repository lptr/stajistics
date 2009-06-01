/* Copyright 2009 The Stajistics Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.stajistics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author The Stajistics Project
 */
public abstract class StatsKeyMatcher implements Serializable {

    private static final long serialVersionUID = -1713747126597353487L;

    public static Builder build() {
        return new Builder();
    }

    public static StatsKeyMatcher all() {
        return AllMatcher.INSTANCE;
    }

    public static StatsKeyMatcher none() {
        return NoneMatcher.INSTANCE;
    }

    public static StatsKeyMatcher not(final StatsKeyMatcher delegate) {
        return new NegationMatcher(delegate);
    }

    public static StatsKeyMatcher prefix(final String prefix) {
        return new PrefixMatcher(MatchTarget.KEY_NAME, prefix);
    }

    public static StatsKeyMatcher attrNamePrefix(final String prefix) {
        return new PrefixMatcher(MatchTarget.ATTR_NAME, prefix);
    }

    public static StatsKeyMatcher attrValuePrefix(final String prefix) {
        return new PrefixMatcher(MatchTarget.ATTR_VALUE, prefix);
    }
    
    public static StatsKeyMatcher suffix(final String suffix) {
        return new SuffixMatcher(MatchTarget.KEY_NAME, suffix);
    }
    
    public static StatsKeyMatcher attrNameSuffix(final String suffix) {
        return new SuffixMatcher(MatchTarget.ATTR_NAME, suffix);
    }

    public static StatsKeyMatcher attrValueSuffix(final String suffix) {
        return new SuffixMatcher(MatchTarget.ATTR_VALUE, suffix);
    }
    
    public static StatsKeyMatcher contains(final String string) {
        return new ContainsMatcher(MatchTarget.KEY_NAME, string);
    }

    public static StatsKeyMatcher attrNameContains(final String string) {
        return new ContainsMatcher(MatchTarget.ATTR_NAME, string);
    }

    public static StatsKeyMatcher attrValueContains(final String string) {
        return new ContainsMatcher(MatchTarget.ATTR_VALUE, string);
    }
    
    public static StatsKeyMatcher depth(final int depth) {
        return new DepthMatcher(depth);
    }

    public static StatsKeyMatcher attributeCount(final int count) {
        return new AttrCountMatcher(count);
    }

    public Collection<StatsKey> filterKeys(final Collection<StatsKey> keys) {
        List<StatsKey> filteredList = new ArrayList<StatsKey>(keys.size());
        for (StatsKey key : keys) {
            if (matches(key)) {
                filteredList.add(key);
            }
        }
        return Collections.unmodifiableCollection(filteredList);
    }

    public <T> Collection<T> filterToCollection(final Map<StatsKey,T> map) {
        List<T> filteredList = new ArrayList<T>(map.size() / 2);
        for (Map.Entry<StatsKey,T> entry : map.entrySet()) {
            if (matches(entry.getKey())) {
                filteredList.add(entry.getValue());
            }
        }
        return Collections.unmodifiableCollection(filteredList);
    }

    public <T> Collection<T> filterToCollection(final Collection<? extends StatsKeyAssociation<T>> associations) {
        List<T> filteredList = new ArrayList<T>(associations.size() / 2);
        StatsKey key;
        for (StatsKeyAssociation<T> ka : associations) {
            key = ka.getKey();
            if (matches(key)) {
                filteredList.add(ka.getValue());
            }
        }
        return Collections.unmodifiableCollection(filteredList);
    }

    public <T> Map<StatsKey,T> filterToMap(final Map<StatsKey,T> map) {
        Map<StatsKey,T> filteredMap = new HashMap<StatsKey,T>(map.size() / 2);
        for (Map.Entry<StatsKey,T> entry : map.entrySet()) {
            if (matches(entry.getKey())) {
                filteredMap.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(filteredMap);
    }

    public <T> Map<StatsKey,T> filterToMap(final Collection<? extends StatsKeyAssociation<T>> associations) {
        Map<StatsKey,T> filteredMap = new HashMap<StatsKey,T>(associations.size() / 2);
        StatsKey key;
        for (StatsKeyAssociation<T> ka : associations) {
            key = ka.getKey();
            if (matches(key)) {
                filteredMap.put(key, ka.getValue());
            }
        }
        return Collections.unmodifiableMap(filteredMap);
    }

    public abstract boolean matches(StatsKey key);

    @Override
    public final boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }        

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        if (!(obj instanceof StatsKeyMatcher)) {
            return false;
        }
        
        return equals((StatsKeyMatcher)obj);
    }

    public abstract boolean equals(StatsKeyMatcher matcher);

    @Override
    public abstract int hashCode();

    /* NESTED CLASSES */

    public static class Builder {

        private final List<StatsKeyMatcher> matchers = new ArrayList<StatsKeyMatcher>(4);
        private boolean negateNext = false;

        protected Builder() {}

        private void addMatcher(StatsKeyMatcher matcher) {
            if (negateNext) {
                negateNext = false;
                matcher = StatsKeyMatcher.not(matcher);
            }

            matchers.add(matcher);
        }

        public Builder not() {
            negateNext = !negateNext;
            return this;
        }

        public Builder withPrefix(final String prefix) {
            addMatcher(prefix(prefix));
            return this;
        }

        public Builder withAttrNamePrefix(final String prefix) {
            addMatcher(attrNamePrefix(prefix));
            return this;
        }

        public Builder withAttValuePrefix(final String prefix) {
            addMatcher(attrValuePrefix(prefix));
            return this;
        }

        public Builder withSuffix(final String suffix) {
            addMatcher(suffix(suffix));
            return this;
        }
        
        public Builder withAttrNameSuffix(final String suffix) {
            addMatcher(attrNameSuffix(suffix));
            return this;
        }

        public Builder withAttrValueSuffix(final String suffix) {
            addMatcher(attrValueSuffix(suffix));
            return this;
        }

        public Builder containing(final String string) {
            addMatcher(contains(string));
            return this;
        }

        public Builder attrNameContaining(final String string) {
            addMatcher(attrNameContains(string));
            return this;
        }

        public Builder attrValueContaining(final String string) {
            addMatcher(attrValueContains(string));
            return this;
        }
        
        public Builder atDepth(final int depth) {
            matchers.add(depth(depth));
            return this;
        }

        public Builder withAttributeCountOf(final int count) {
            matchers.add(attributeCount(count));
            return this;
        }

        public StatsKeyMatcher matcher() {

            if (matchers.isEmpty()) {
                return NoneMatcher.INSTANCE;
            }

            if (matchers.size() == 1) {
                return matchers.get(0);
            }

            return new CompositeMatcher(matchers);
        }
    }

    private enum MatchTarget {
        KEY_NAME,
        ATTR_NAME,
        ATTR_VALUE
    }

    private static class NegationMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = -4636379636104878297L;
        
        private final StatsKeyMatcher delegate;

        NegationMatcher(final StatsKeyMatcher delegate) {
            if (delegate == null) {
                throw new NullPointerException("delegate");
            }

            this.delegate = delegate;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return !delegate.matches(key);
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            return delegate.equals(((NegationMatcher)other).delegate);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ delegate.hashCode();
        }
    }

    private static class CompositeMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = 8608713955660143865L;
        
        private final List<StatsKeyMatcher> matchers;

        CompositeMatcher(final List<StatsKeyMatcher> matchers) {
            if (matchers == null) {
                throw new NullPointerException("matchers");
            }
            if (matchers.isEmpty()) {
                throw new IllegalArgumentException("empty matchers");
            }

            this.matchers = matchers;
        }

        @Override
        public boolean matches(final StatsKey key) {
            int size = matchers.size();
            for (int i = 0; i < size; i++) {
                if (!matchers.get(i).matches(key)) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            return matchers.equals(((CompositeMatcher)other).matchers);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ matchers.hashCode();
        }        
    }

    private static class AllMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = 6746094078692655632L;

        private static final StatsKeyMatcher INSTANCE = new AllMatcher();

        @Override
        public boolean matches(final StatsKey key) {
            return true;
        }

        @Override
        public Collection<StatsKey> filterKeys(final Collection<StatsKey> keys) {
            return Collections.unmodifiableCollection(new ArrayList<StatsKey>(keys));
        }

        @Override
        public <T> Collection<T> filterToCollection(final Map<StatsKey,T> map) {
            return Collections.unmodifiableCollection(new ArrayList<T>(map.values()));
        }
        
        @Override
        public <T> Map<StatsKey,T> filterToMap(final Map<StatsKey,T> map) {
            return Collections.unmodifiableMap(map);
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            return other == INSTANCE;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static class NoneMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = -4349112693642153977L;

        private static final StatsKeyMatcher INSTANCE = new NoneMatcher();

        @Override
        public boolean matches(final StatsKey other) {
            return false;
        }

        @Override
        public Collection<StatsKey> filterKeys(final Collection<StatsKey> keys) {
            return Collections.emptyList();
        }

        @Override
        public <T> Collection<T> filterToCollection(final Map<StatsKey,T> map) {
            return Collections.emptyList();
        }

        @Override
        public <T> Collection<T> filterToCollection(final Collection<? extends StatsKeyAssociation<T>> associations) {
            return Collections.emptyList();
        }

        @Override
        public <T> Map<StatsKey, T> filterToMap(final Map<StatsKey,T> map) {
            return Collections.emptyMap();
        }
        
        @Override
        public <T> Map<StatsKey,T> filterToMap(final Collection<? extends StatsKeyAssociation<T>> associations) {
            return Collections.emptyMap();
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            return other == INSTANCE;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }

    private static class PrefixMatcher extends StatsKeyMatcher {
        private static final long serialVersionUID = -7895894285409549318L;

        private final MatchTarget target;
        private final String prefix;

        PrefixMatcher(final MatchTarget target, final String prefix) {
            if (target == null) {
                throw new NullPointerException("target");
            }
            if (prefix == null) {
                throw new NullPointerException("prefix");
            }

            this.target = target;
            this.prefix = prefix;
        }

        @Override
        public boolean matches(final StatsKey key) {
            switch (target) {
            case KEY_NAME:
                return key.getName().startsWith(prefix);
            case ATTR_NAME:
                for (String attrName : key.getAttributes().keySet()) {
                    if (attrName.startsWith(prefix)) {
                        return true;
                    }
                }
                break;
            case ATTR_VALUE:
                for (Object attrValue : key.getAttributes().values()) {
                    if (attrValue.toString().startsWith(prefix)) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            PrefixMatcher prefixMatcher = (PrefixMatcher)other;
            return target == prefixMatcher.target &&
                   prefix.equals(prefixMatcher.prefix);
        }        

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ prefix.hashCode();
        }
    }

    private static class SuffixMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = -1759972536606378962L;

        private final MatchTarget target;
        private final String suffix;

        SuffixMatcher(final MatchTarget target, final String suffix) {
            if (target == null) {
                throw new NullPointerException("target");
            }
            if (suffix == null) {
                throw new NullPointerException("suffix");
            }

            this.target = target;
            this.suffix = suffix;
        }

        @Override
        public boolean matches(final StatsKey key) {
            switch (target) {
            case KEY_NAME:
                return key.getName().endsWith(suffix);
            case ATTR_NAME:
                for (String attrName : key.getAttributes().keySet()) {
                    if (attrName.endsWith(suffix)) {
                        return true;
                    }
                }
                break;
            case ATTR_VALUE:
                for (Object attrValue : key.getAttributes().values()) {
                    if (attrValue.toString().endsWith(suffix)) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            SuffixMatcher suffixMatcher = (SuffixMatcher)other;
            return target == suffixMatcher.target &&
                   suffix.equals(suffixMatcher.suffix);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ suffix.hashCode();
        }
    }

    private static class ContainsMatcher extends StatsKeyMatcher {
        
        private static final long serialVersionUID = -7458113352734576005L;

        private final MatchTarget target;
        private final String string;

        ContainsMatcher(final MatchTarget target, final String string) {
            if (target == null) {
                throw new NullPointerException("target");
            }
            if (string == null) {
                throw new NullPointerException("string");
            }

            this.target = target;
            this.string = string;
        }

        @Override
        public boolean matches(final StatsKey key) {
            switch (target) {
            case KEY_NAME:
                return key.getName().indexOf(string) > -1;
            case ATTR_NAME:
                for (String attrName : key.getAttributes().keySet()) {
                    if (attrName.indexOf(string) > -1) {
                        return true;
                    }
                }
                break;
            case ATTR_VALUE:
                for (Object attrValue : key.getAttributes().values()) {
                    if (attrValue.toString().indexOf(string) > -1) {
                        return true;
                    }
                }
                break;
            }

            return false;
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            ContainsMatcher containsMatcher = (ContainsMatcher)other;
            return target == containsMatcher.target &&
                    string.equals(containsMatcher.string);
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ string.hashCode();
        }
    }

    private static class DepthMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = 8257312819320924383L;
        
        private final int depth;

        DepthMatcher(int depth) {
            if (depth < 1) {
                depth = 1;
            }
            this.depth = depth;
        }

        @Override
        public boolean matches(final StatsKey key) {
            int count = countHeirarchyDelimiters(key.getName()) - 1;
            return depth == count;
        }

        private int countHeirarchyDelimiters(final String name) {
            int count = 0;
            final char[] chars = name.toCharArray();

            for (int i = 0; i < chars.length; i++) {
                if (chars[i] == StatsConstants.KEY_HIERARCHY_DELIMITER) {
                    count++;
                }
            }

            return count;
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            return depth == ((DepthMatcher)other).depth;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ depth;
        }
    }

    private static class AttrCountMatcher extends StatsKeyMatcher {

        private static final long serialVersionUID = -3050588776658645358L;
        
        private final int count;

        public AttrCountMatcher(int count) {
            if (count < 0) {
                count = 0;
            }

            this.count = count;
        }

        @Override
        public boolean matches(final StatsKey key) {
            return count == key.getAttributeCount();
        }

        @Override
        public boolean equals(final StatsKeyMatcher other) {
            if (!(other instanceof AttrCountMatcher)) {
                return false;
            }

            return count == ((AttrCountMatcher)other).count;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode() ^ count; 
        }
        
    }
}
