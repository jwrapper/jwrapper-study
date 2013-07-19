package jwrapper.archive;

import java.io.File;

public abstract interface FileStripper
{
  public abstract String getName();

  public abstract long getStrippedTotal();

  public abstract void addToStrippedTotal(long paramLong);

  public abstract boolean canLeaveOutFile(File paramFile);

  public abstract boolean canLeaveOutFile(File paramFile, String[] paramArrayOfString);
}

/* Location:           /tmp/jwrapper/jwrapper-00020654351/lib/jwrapper_utils.jar
 * Qualified Name:     jwrapper.archive.FileStripper
 * JD-Core Version:    0.6.2
 */