-- SQL Script to create the `profiles` table in Supabase
-- Run this script in the Supabase SQL Editor

CREATE TABLE public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    username TEXT UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Set up Row Level Security (RLS)
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;

-- Allow users to read all profiles (to check uniqueness)
CREATE POLICY "Profiles are viewable by everyone" ON public.profiles
    FOR SELECT USING (true);

-- Allow authenticated users to insert their own profile
CREATE POLICY "Users can insert their own profile" ON public.profiles
    FOR INSERT WITH CHECK (auth.uid() = id);

-- Trigger to automatically create a profile row when a user signs up
-- NOTE: In the app, we are setting username as part of onboarding. 
-- A trigger might be too early if the username is not set during initial auth.signUp.
-- So we won't use a trigger. The app should insert the profile after taking the username.
