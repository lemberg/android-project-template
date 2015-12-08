package com.ls.templateproject.model.data.base;

public class AbstractVO<IdClass>
{
    protected IdClass id;

    public void setId(final IdClass theId)
    {
        this.id = theId;
    }

    public IdClass getId()
    {
        return this.id;
    }
}
