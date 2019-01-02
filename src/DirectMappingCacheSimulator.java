import java.io.File;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class DirectMappingCacheSimulator
{
    private double mainMemorySize; 			//i.e. N
    private int mainMemoryBlockSize; 		//i.e. M
    private int cacheBlockCount; 			//i.e. L
    private String referenceWordsFileName;

    private double indexLength;
    private double offsetLength;
    private double tagLength;

    private Queue<MyCacheBlock> cacheBlockQueue;
    private int hitCount;
    private int missCount;
    private int compulsoryMissCount;

    private boolean IS_DEBUG = false;

    public DirectMappingCacheSimulator(int n, int L, int M, String referenceWordsFileName, boolean IS_DEBUG)
    {
        this.mainMemorySize = Math.pow(2, n);
        this.mainMemoryBlockSize = M;
        this.cacheBlockCount = L;
        this.referenceWordsFileName = referenceWordsFileName;

        this.cacheBlockQueue =  new LinkedList<>();

        this.offsetLength = Math.ceil(logOfBase(2, this.mainMemoryBlockSize));
        this.indexLength = Math.ceil(logOfBase(2, this.cacheBlockCount));
        this.tagLength = getRefWordLength(this.referenceWordsFileName) - this.offsetLength - this.indexLength;

        this.IS_DEBUG = IS_DEBUG;
    }

    private double getRefWordLength(String referenceWordsFileName)
    {
        try
        {
            File fileToRead = new File(referenceWordsFileName);
            Scanner sc = new Scanner(fileToRead);

            while (sc.hasNextLine())
            {
                int len = getBinaryString(sc.nextLine()).length();
                sc.close();
                return len;
            }
        }
        catch (Exception ex)
        {
        }
        return 0;
    }

    private double logOfBase(int base, int num)
    {
        return Math.log(num) / Math.log(base);
    }

    private String PadFront(int totalSize, String str, String padCharacter)
    {
        int initialLength = str.length();
        for (int i = 0; i < totalSize - initialLength; i++)
            str = padCharacter + str;
        return str;
    }

    private String getBinaryString(String hexValue)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < hexValue.length(); i++)
        {
            String binary = new BigInteger(hexValue.substring(i, i+1), 16).toString(2);
            stringBuilder.append(PadFront(4, binary, "0"));
        }
        return stringBuilder.toString();
    }

    private boolean IsHit(MyCacheBlock myCacheBlockToCheck)
    {
        for (MyCacheBlock myCacheBlock : cacheBlockQueue)
        {
            if (myCacheBlock.equals(myCacheBlockToCheck))
                return true;
        }
        return false;
    }

    private boolean IsMiss(MyCacheBlock myCacheBlockToCheck)
    {
        for (MyCacheBlock myCacheBlock : cacheBlockQueue)
        {
            if (!myCacheBlock.getTag().equals(myCacheBlockToCheck.getTag()) && myCacheBlock.getIndex().equals(myCacheBlockToCheck.getIndex()))
                return true;
        }
        return false;
    }

    private MyCacheBlock GetMissedCacheBlock(MyCacheBlock myCacheBlockToCheck)
    {
        MyCacheBlock missedCacheBlock = null;

        if (cacheBlockQueue.size() == 0)
            return myCacheBlockToCheck;

        for (MyCacheBlock myCacheBlock : cacheBlockQueue)
        {
            if (!myCacheBlock.getTag().equals(myCacheBlockToCheck.getTag()) && myCacheBlock.getIndex().equals(myCacheBlockToCheck.getIndex()))
                missedCacheBlock = myCacheBlock;
        }
        return missedCacheBlock;
    }

    private String getDisplayBinaryString(String binaryVal)
    {
        int dashCount = 0;
        int x = 4;
        while(dashCount < 5)
        {
            binaryVal = binaryVal.substring(0, x) + "-" + binaryVal.substring(x);
            dashCount++;
            x += 5;
        }
        return binaryVal;
    }

    private boolean IsCompulsoryMiss(MyCacheBlock myCacheBlockToCheck)
    {
        boolean found = false;
        for (MyCacheBlock myCacheBlock : cacheBlockQueue)
        {
            if (myCacheBlock.getIndex().equals(myCacheBlockToCheck.getIndex()))
                found = true;
        }
        return (found == false);
    }

    public void Simulate()
    {
        try
        {
            File fileToRead = new File(this.referenceWordsFileName);
            Scanner sc = new Scanner(fileToRead);
            String inputReferenceWord;

            while (sc.hasNextLine())
            {
                inputReferenceWord = sc.nextLine();
                String initialHex = inputReferenceWord;
                inputReferenceWord = getBinaryString(inputReferenceWord);

                MyCacheBlock cacheToCheck = new MyCacheBlock();
                int tagIndex = (int) this.tagLength;
                int indexIndex = (int) this.tagLength + (int) this.indexLength;
                int offsetIndex = (int) this.tagLength + (int) this.indexLength + (int) this.offsetLength;

                cacheToCheck.setTag(inputReferenceWord.substring(0, tagIndex));
                cacheToCheck.setIndex(inputReferenceWord.substring(tagIndex, indexIndex));
                cacheToCheck.setOffset(inputReferenceWord.substring(indexIndex, offsetIndex));

                if(IsHit(cacheToCheck))
                {
                    if (IS_DEBUG)
                        System.out.println(initialHex +" Hit   " +getDisplayBinaryString(inputReferenceWord));

                    hitCount++;
                }
                else if(IsCompulsoryMiss(cacheToCheck))
                {
                    if (IS_DEBUG)
                        System.out.println(initialHex +" CM    " +getDisplayBinaryString(inputReferenceWord));

                    compulsoryMissCount++;

                    //Add to Cache
                    if (cacheBlockQueue.size() == this.cacheBlockCount)
                        cacheBlockQueue.remove();

                    cacheBlockQueue.add(cacheToCheck);
                }
                else if(IsMiss(cacheToCheck))
                {
                    if (IS_DEBUG)
                        System.out.println(initialHex +" Miss  " +getDisplayBinaryString(inputReferenceWord));

                    missCount++;

                    MyCacheBlock missedCacheBlock = GetMissedCacheBlock(cacheToCheck);
                    missedCacheBlock.setTag(cacheToCheck.getTag());
                }
            }

            PrintStatistics();
        }
        catch (Exception ex)
        {
        }
    }

    private void PrintStatistics()
    {
        int totalRef = hitCount + compulsoryMissCount + missCount;
        System.out.println("Hit Ratio : " + ((double) hitCount / totalRef));
        System.out.println("CM Ratio : " + ((double) compulsoryMissCount / totalRef));
        System.out.println("Miss Ratio : " + ((double) missCount / totalRef));
    }
}