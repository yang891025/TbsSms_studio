package com.tbs.tbssms.task;

public interface ProgressCallable<T> {
    public T call(final IProgressListener pProgressListener) throws Exception;
}
