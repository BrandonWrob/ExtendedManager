#!/usr/bin/env python3
# generate_lcov_badge.py
# Coverage badge generating script for lcov.info file used in Istanbul coverage reporter for frontend tests

import sys
import math
import os

badgeTemplate = '''<svg xmlns="http://www.w3.org/2000/svg" width="104"
height="20" role="img" aria-label="{3}: {0}">
  <linearGradient id="s" x2="0" y2="100%">
    <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
    <stop offset="1" stop-opacity=".1"/>
  </linearGradient>
  <clipPath id="r">
    <rect width="104" height="20" rx="3" fill="#fff"/>
  </clipPath>
  <g clip-path="url(#r)">
    <rect width="61" height="20" fill="#555"/>
    <rect x="61" width="43" height="20" fill="{1}"/>
    <rect width="104" height="20" fill="url(#s)"/>
  </g>
  <g fill="#fff" text-anchor="middle"
    font-family="Verdana,Geneva,DejaVu Sans,sans-serif"
    text-rendering="geometricPrecision" font-size="110">
    <text aria-hidden="true" x="315" y="150" fill="#010101"
      fill-opacity=".3" transform="scale(.1)" textLength="510">{3}</text>
    <text x="315" y="140" transform="scale(.1)" fill="#fff"
      textLength="510">{3}</text>
    <text aria-hidden="true" x="815" y="150"
      fill="#010101" fill-opacity=".3" transform="scale(.1)"
      textLength="{2}">{0}</text>
    <text x="815" y="140"
      transform="scale(.1)" fill="#fff" textLength="{2}">{0}</text>
  </g>
</svg>'''

defaultColors = ["#4c1", "#97ca00", "#a4a61d", "#dfb317", "#fe7d37", "#e05d44"]

def generateBadge(covStr, color, badgeType="coverage"):
    """Generates the badge as a string."""
    if len(covStr) >= 6:
        textLength = "380"
    elif len(covStr) >= 5:
        textLength = "330"
    elif len(covStr) >= 4:
        textLength = "250"
    else:
        textLength = "170"
    return badgeTemplate.format(covStr, color, textLength, badgeType)

def parseLCOVFile(filename):
    """Parses an lcov.info file and calculates line and branch coverage."""
    total_lines = 0
    covered_lines = 0
    total_branches = 0
    covered_branches = 0
    with open(filename, 'r') as f:
        for line in f:
            if line.startswith('DA:'):
                parts = line.strip().split(':')[1].split(',')
                if len(parts) == 2:
                    line_number, execution_count = parts
                    total_lines += 1
                    if int(execution_count) > 0:
                        covered_lines += 1
            elif line.startswith('BRDA:'):
                parts = line.strip().split(':')[1].split(',')
                if len(parts) == 4:
                    _, _, _, taken = parts
                    total_branches += 1
                    if taken != '-' and int(taken) > 0:
                        covered_branches += 1
    if total_lines == 0:
        line_coverage = 1.0  # Assume full coverage if no lines
    else:
        line_coverage = covered_lines / total_lines
    if total_branches == 0:
        branch_coverage = 1.0  # Assume full coverage if no branches
    else:
        branch_coverage = covered_branches / total_branches
    return line_coverage, branch_coverage

def coverageTruncatedToString(coverage, decimals=2):
    """Converts the coverage percentage to a formatted string with up to two decimal places."""
    # Multiply by 100 to convert to percentage
    coverage_percentage = coverage * 100
    # Truncate to two decimal places
    coverage_percentage = math.floor(coverage_percentage * 100) / 100
    if coverage_percentage - int(coverage_percentage) == 0:
        covStr = "{0:d}%".format(int(coverage_percentage))
    else:
        covStr = "{0:.2f}%".format(coverage_percentage)
    return covStr, coverage_percentage

def badgeCoverageStringColorPair(coverage, cutoffs=[100, 90, 80, 70, 60], colors=[]):
    """Converts the coverage percentage to a formatted string, and determines the badge color."""
    if not colors:
        colors = defaultColors
    covStr, coverage_percentage = coverageTruncatedToString(coverage)
    c = computeColorIndex(coverage_percentage, cutoffs, len(colors))
    return covStr, colors[c]

def computeColorIndex(coverage, cutoffs, numColors):
    """Computes index into color list from coverage."""
    numIntervals = min(numColors, len(cutoffs)+1)
    for c in range(numIntervals-1):
        if coverage >= cutoffs[c]:
            return c
    return numIntervals-1

def createOutputDirectories(badgesDirectory):
    """Creates the output directory if it doesn't already exist."""
    if not os.path.exists(badgesDirectory):
        os.makedirs(badgesDirectory, exist_ok=True)

def main():
    if len(sys.argv) < 5:
        print("Usage: generate_lcov_badge.py <lcov_file> <badges_directory> <coverage_badge_filename> <branch_coverage_badge_filename>")
        sys.exit(1)
    lcovFile = sys.argv[1]
    badgesDirectory = sys.argv[2]
    coverageFilename = sys.argv[3]
    branchCoverageFilename = sys.argv[4]
    colorCutoffs = [100, 90, 80, 70, 60]
    colors = defaultColors

    line_coverage, branch_coverage = parseLCOVFile(lcovFile)

    # Generate line coverage badge
    covStr, color = badgeCoverageStringColorPair(line_coverage, colorCutoffs, colors)
    if badgesDirectory:
        createOutputDirectories(badgesDirectory)
    coverageBadgePath = os.path.join(badgesDirectory, coverageFilename)
    with open(coverageBadgePath, "w") as badge:
        badge.write(generateBadge(covStr, color, badgeType="coverage"))
    print("Generated line coverage badge at:", coverageBadgePath)

    # Generate branch coverage badge
    branchStr, branch_color = badgeCoverageStringColorPair(branch_coverage, colorCutoffs, colors)
    branchCoverageBadgePath = os.path.join(badgesDirectory, branchCoverageFilename)
    with open(branchCoverageBadgePath, "w") as badge:
        badge.write(generateBadge(branchStr, branch_color, badgeType="branches"))
    print("Generated branch coverage badge at:", branchCoverageBadgePath)

if __name__ == "__main__":
    main()
