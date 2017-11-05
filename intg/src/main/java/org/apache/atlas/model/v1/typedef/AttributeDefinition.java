/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.atlas.model.v1.typedef;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Objects;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.PUBLIC_ONLY;


@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AttributeDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String       name;
    private String       dataTypeName;
    private Multiplicity multiplicity;
    private boolean      isComposite; // A composite is the one whose lifecycle is dependent on the enclosing type and is not just a reference
    private boolean      isUnique;
    private boolean      isIndexable;
    private String       reverseAttributeName; // If this is a reference attribute, then the name of the attribute on the Class that this refers to.



    public AttributeDefinition() {
    }

    public AttributeDefinition(String name, String dataTypeName, Multiplicity multiplicity) {
        this(name, dataTypeName, multiplicity, false, false, true, null);
    }

    public AttributeDefinition(String name, String dataTypeName, Multiplicity multiplicity, boolean isComposite,
                               String reverseAttributeName) {
        this(name, dataTypeName, multiplicity, isComposite, false, false, reverseAttributeName);
    }

    public AttributeDefinition(String name, String dataTypeName, Multiplicity multiplicity, boolean isComposite, boolean isUnique, boolean isIndexable, String reverseAttributeName) {
        this.name                 = name;
        this.dataTypeName         = dataTypeName;
        this.multiplicity         = multiplicity;
        this.isComposite          = isComposite;
        this.isUnique             = isUnique;
        this.isIndexable          = isIndexable;
        this.reverseAttributeName = reverseAttributeName;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataTypeName() {
        return dataTypeName;
    }

    public void setDataTypeName(String dataTypeName) {
        this.dataTypeName = dataTypeName;
    }

    public Multiplicity getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(Multiplicity multiplicity) {
        this.multiplicity = multiplicity;
    }

    public boolean getIsComposite() {
        return isComposite;
    }

    public void setIsComposite(boolean isComposite) {
        this.isComposite = isComposite;
    }

    public boolean getIsUnique() {
        return isUnique;
    }

    public void setIsUnique(boolean isUnique) {
        this.isUnique = isUnique;
    }

    public boolean getIsIndexable() {
        return isIndexable;
    }

    public void setIsIndexable(boolean isIndexable) {
        this.isIndexable = isIndexable;
    }

    public String getReverseAttributeName() {
        return reverseAttributeName;
    }

    public void setReverseAttributeName(String reverseAttributeName) {
        this.reverseAttributeName = reverseAttributeName;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AttributeDefinition that = (AttributeDefinition) o;

        return isComposite == that.isComposite &&
               isUnique == that.isUnique &&
               isIndexable == that.isIndexable &&
               Objects.equals(name, that.name) &&
               Objects.equals(dataTypeName, that.dataTypeName) &&
               Objects.equals(multiplicity, that.multiplicity) &&
               Objects.equals(reverseAttributeName, that.reverseAttributeName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, dataTypeName, multiplicity, isComposite, isUnique, isIndexable, reverseAttributeName);
    }
}