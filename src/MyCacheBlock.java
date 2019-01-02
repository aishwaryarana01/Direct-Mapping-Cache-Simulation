public class MyCacheBlock
{
    private String tag;
    private String index;
    private String offset;

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = tag;
    }

    public String getIndex()
    {
        return index;
    }

    public void setIndex(String index)
    {
        this.index = index;
    }

    public String getOffset()
    {
        return offset;
    }

    public void setOffset(String offset)
    {
        this.offset = offset;
    }

    @Override
    public boolean equals(Object myCacheBlock)
    {
        return ((MyCacheBlock) myCacheBlock).getTag().equals(this.tag) && ((MyCacheBlock) myCacheBlock).getIndex().equals(this.index);
    }
}